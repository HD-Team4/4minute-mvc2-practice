document.addEventListener('DOMContentLoaded', () => {
    const tableBody = document.querySelector('#boardTableBody');
    const boardForm = document.querySelector('#boardForm');
    const detail = document.querySelector('#boardDetail');
    const editForm = document.querySelector('#boardEditForm');
    const commentList = document.querySelector('#commentList');
    const commentForm = document.querySelector('#commentForm');
    const reloadCommentsButton = document.querySelector('#reloadCommentsButton');
    const deleteBoardButton = document.querySelector('#deleteBoardButton');
    const mediaFiles = document.querySelector('#mediaFiles');

    initEmoticonPicker();
    if (mediaFiles) mediaFiles.addEventListener('change', previewMediaFiles);
    if (tableBody) loadBoards();
    if (boardForm) boardForm.addEventListener('submit', createBoard);
    if (detail) loadBoardDetail(detail.dataset.boardId);
    if (editForm) {
        loadBoardForEdit(document.querySelector('#boardId').value);
        editForm.addEventListener('submit', updateBoard);
    }
    if (deleteBoardButton) deleteBoardButton.addEventListener('click', deleteBoard);
    if (commentList) loadComments(commentList.dataset.boardId);
    if (reloadCommentsButton) reloadCommentsButton.addEventListener('click', () => loadComments(commentList.dataset.boardId));
    if (commentForm) commentForm.addEventListener('submit', createComment);
    if (commentList) commentList.addEventListener('click', deleteComment);
    document.querySelector('#existingMediaList')?.addEventListener('click', removeExistingMedia);
});

function initEmoticonPicker() {
    const toolbar = document.querySelector('.emoticon-toolbar');
    const content = document.querySelector('#content');

    if (!toolbar || !content) return;

    toolbar.addEventListener('click', (event) => {
        const button = event.target.closest('button[data-emoticon]');
        if (!button) return;

        insertAtCursor(content, button.dataset.emoticon);
    });
}

function insertAtCursor(textarea, value) {
    const start = textarea.selectionStart ?? textarea.value.length;
    const end = textarea.selectionEnd ?? textarea.value.length;
    const before = textarea.value.slice(0, start);
    const after = textarea.value.slice(end);

    textarea.value = `${before}${value}${after}`;
    textarea.focus();
    textarea.setSelectionRange(start + value.length, start + value.length);
}

async function loadBoards() {
    const tableBody = document.querySelector('#boardTableBody');
    try {
        const response = await fetch(`${getContextPath()}/board/api/list`);
        if (!response.ok) {
            const result = await safeJson(response);
            throw new Error(result.message || 'Failed to load board list.');
        }
        const boards = await response.json();
        if (!boards.length) {
            tableBody.innerHTML = '<tr><td colspan="6" class="text-center text-muted py-4">등록된 글이 없습니다.</td></tr>';
            return;
        }
        tableBody.innerHTML = boards.map((board) => `
            <tr>
                <td>${board.boardId}</td>
                <td><a href="${getContextPath()}/board/detail.do?boardId=${board.boardId}">${escapeHtml(board.title)}</a></td>
                <td>${escapeHtml(board.writer)}</td>
                <td>댓글(${board.commentCount || 0})</td>
                <td>${board.readCount}</td>
                <td>${escapeHtml(board.createdAt)}</td>
            </tr>
        `).join('');
    } catch (error) {
        tableBody.innerHTML = `<tr><td colspan="6" class="text-center text-danger py-4">${escapeHtml(error.message)}</td></tr>`;
    }
}

async function createBoard(event) {
    event.preventDefault();
    try {
        const result = await requestBoard('/board/api/create', new FormData(event.target));
        showBoardMessage(result.message, result.success);
        if (result.success) location.href = `${getContextPath()}/board/list.do`;
    } catch (error) {
        showBoardMessage(error.message, false);
    }
}

async function loadBoardDetail(boardId) {
    const detail = document.querySelector('#boardDetail');
    try {
        const response = await fetch(`${getContextPath()}/board/api/detail?boardId=${encodeURIComponent(boardId)}`);
        if (!response.ok) {
            const result = await safeJson(response);
            throw new Error(result.message || 'Failed to load board.');
        }
        const board = await response.json();
        detail.innerHTML = `
            <h3>${escapeHtml(board.title)}</h3>
            <div class="text-muted small mb-3">
                ${escapeHtml(board.writer)} / ${escapeHtml(board.createdAt)} / 조회수 ${board.readCount}
            </div>
            <div class="board-content">${renderBoardContent(board.content)}</div>
        `;
    } catch (error) {
        detail.innerHTML = `<div class="text-danger">${escapeHtml(error.message)}</div>`;
    }
}

async function loadBoardForEdit(boardId) {
    const editForm = document.querySelector('#boardEditForm');
    if (!editForm) return;

    try {
        const response = await fetch(`${getContextPath()}/board/api/edit?boardId=${encodeURIComponent(boardId)}`);
        if (!response.ok) {
            const result = await safeJson(response);
            throw new Error(result.message || 'Failed to load board.');
        }
        const board = await response.json();
        document.querySelector('#writer').value = board.writer;
        document.querySelector('#title').value = board.title;
        const parsed = splitContentAndMedia(board.content);
        document.querySelector('#content').value = parsed.content;
        renderExistingMedia(parsed.media);
    } catch (error) {
        showBoardMessage(error.message, false);
    }
}

async function updateBoard(event) {
    event.preventDefault();
    const boardId = document.querySelector('#boardId').value;
    try {
        const result = await requestBoard('/board/api/update', new FormData(event.target));
        showBoardMessage(result.message, result.success);
        if (result.success) location.href = `${getContextPath()}/board/detail.do?boardId=${boardId}`;
    } catch (error) {
        showBoardMessage(error.message, false);
    }
}

async function deleteBoard() {
    const boardId = new URLSearchParams(location.search).get('boardId') || document.querySelector('#boardId')?.value;
    const password = prompt('게시글 비밀번호를 입력하세요.');
    if (!boardId || !password) return;

    const formData = new FormData();
    formData.append('boardId', boardId);
    formData.append('password', password);

    try {
        const result = await requestBoard('/board/api/delete', formData);
        showBoardMessage(result.message, result.success);
        if (result.success) location.href = `${getContextPath()}/board/list.do`;
    } catch (error) {
        showBoardMessage(error.message, false);
    }
}

async function loadComments(boardId) {
    const commentList = document.querySelector('#commentList');
    if (!commentList) return;

    try {
        const response = await fetch(`${getContextPath()}/board/api/comments?boardId=${encodeURIComponent(boardId)}`);
        if (!response.ok) {
            const result = await safeJson(response);
            throw new Error(result.message || 'Failed to load comments.');
        }
        const comments = await response.json();
        renderComments(comments);
    } catch (error) {
        commentList.innerHTML = `<p class="text-danger mb-0">${escapeHtml(error.message)}</p>`;
    }
}

async function createComment(event) {
    event.preventDefault();
    const form = event.target;
    const boardId = form.boardId.value;

    try {
        const result = await requestBoard('/board/api/comments/create', new FormData(form));
        showCommentMessage(result.message, result.success);
        if (result.success) {
            form.reset();
            form.boardId.value = boardId;
            await loadComments(boardId);
        }
    } catch (error) {
        showCommentMessage(error.message, false);
    }
}

async function deleteComment(event) {
    const button = event.target.closest('button[data-comment-id]');
    if (!button) return;

    const password = prompt('댓글 비밀번호를 입력하세요.');
    if (!password) return;

    const boardId = document.querySelector('#commentList').dataset.boardId;
    const formData = new FormData();
    formData.append('commentId', button.dataset.commentId);
    formData.append('password', password);

    try {
        const result = await requestBoard('/board/api/comments/delete', formData);
        showCommentMessage(result.message, result.success);
        if (result.success) await loadComments(boardId);
    } catch (error) {
        showCommentMessage(error.message, false);
    }
}

function renderComments(comments) {
    const commentList = document.querySelector('#commentList');
    if (!comments.length) {
        commentList.innerHTML = '<p class="text-muted mb-0">등록된 댓글이 없습니다.</p>';
        return;
    }

    commentList.innerHTML = comments.map((comment) => `
        <div class="comment-item border-bottom py-3">
            <div class="d-flex justify-content-between align-items-start gap-3">
                <div>
                    <strong>${escapeHtml(comment.writer)}</strong>
                    <span class="text-muted small ms-2">${escapeHtml(comment.createdAt)}</span>
                </div>
                <button class="btn btn-sm btn-outline-danger" type="button" data-comment-id="${comment.boardId}">삭제</button>
            </div>
            <p class="mb-0 mt-2 board-content">${escapeHtml(comment.content).replaceAll('\n', '<br>')}</p>
        </div>
    `).join('');
}

async function requestBoard(path, formData) {
    const hasFile = Array.from(formData.values()).some((value) => value instanceof File && value.size > 0);
    const options = hasFile
        ? {method: 'POST', body: formData}
        : {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'},
            body: new URLSearchParams(formData)
        };
    const response = await fetch(`${getContextPath()}${path}`, options);
    const result = await safeJson(response);
    if (!response.ok) throw new Error(result.message || 'Request failed.');
    return result;
}

function previewMediaFiles() {
    const input = document.querySelector('#mediaFiles');
    const preview = document.querySelector('#mediaPreview');
    if (!input || !preview) return;

    const files = Array.from(input.files);
    if (!files.length) {
        preview.innerHTML = '';
        return;
    }

    preview.innerHTML = files.map((file) => {
        const url = URL.createObjectURL(file);
        if (file.type.startsWith('image/')) {
            return `<div class="media-preview-item"><img src="${url}" alt="${escapeHtml(file.name)}"><span>${escapeHtml(file.name)}</span></div>`;
        }
        if (file.type.startsWith('video/')) {
            return `<div class="media-preview-item"><video src="${url}" controls muted></video><span>${escapeHtml(file.name)}</span></div>`;
        }
        return `<div class="media-preview-item text-danger">${escapeHtml(file.name)} - unsupported</div>`;
    }).join('');
}

function splitContentAndMedia(content) {
    const mediaPattern = /\[\[(image|video):([^\]]+)]]/g;
    const media = [];
    const text = String(content ?? '').replace(mediaPattern, (match, type, url) => {
        media.push({type, url, markup: match});
        return '';
    }).replace(/\n{3,}/g, '\n\n').trim();

    return {content: text, media};
}

function renderExistingMedia(media) {
    const list = document.querySelector('#existingMediaList');
    if (!list) return;

    if (!media.length) {
        list.innerHTML = '';
        return;
    }

    list.innerHTML = media.map((item, index) => {
        const preview = item.type === 'image'
            ? `<img src="${escapeHtml(item.url)}" alt="attached image">`
            : `<video src="${escapeHtml(item.url)}" controls muted></video>`;

        return `
            <div class="media-preview-item" data-existing-media-index="${index}">
                ${preview}
                <span>${item.type === 'image' ? '기존 사진' : '기존 동영상'}</span>
                <input type="hidden" name="existingMedia" value="${escapeHtml(item.markup)}">
                <button class="btn btn-sm btn-outline-danger w-100 mt-2" type="button" data-action="remove-existing-media">삭제</button>
            </div>
        `;
    }).join('');
}

function removeExistingMedia(event) {
    const button = event.target.closest('button[data-action="remove-existing-media"]');
    if (!button) return;

    button.closest('.media-preview-item')?.remove();
}

function renderBoardContent(content) {
    const escaped = escapeHtml(content);
    return escaped
        .replaceAll('\n', '<br>')
        .replace(/\[\[image:([^\]]+)]]/g, '<figure class="board-media"><img src="$1" alt="uploaded image"></figure>')
        .replace(/\[\[video:([^\]]+)]]/g, '<figure class="board-media"><video src="$1" controls preload="metadata"></video></figure>');
}

function showBoardMessage(message, success) {
    const box = document.querySelector('#boardMessage');
    if (!box) return;
    box.textContent = message;
    box.className = `alert ${success ? 'alert-success' : 'alert-danger'}`;
}

function showCommentMessage(message, success) {
    const box = document.querySelector('#commentMessage');
    if (!box) return;
    box.textContent = message;
    box.className = `alert ${success ? 'alert-success' : 'alert-danger'}`;
}

async function safeJson(response) {
    try { return await response.json(); } catch (error) { return {}; }
}

function escapeHtml(value) {
    return String(value ?? '').replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;').replaceAll('"', '&quot;').replaceAll("'", '&#039;');
}

function getContextPath() {
    if (window.CONTEXT_PATH !== undefined) return window.CONTEXT_PATH;
    const path = window.location.pathname;
    const index = path.indexOf('/', 1);
    return index === -1 ? '' : path.substring(0, index);
}
