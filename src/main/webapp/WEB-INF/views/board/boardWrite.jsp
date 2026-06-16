<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/include/header.jsp" %>
<main class="container py-4">
    <section class="card border-0 shadow-sm mb-4">
        <div class="card-header bg-white"><h2 class="h5 mb-1">게시글 작성</h2><p class="text-muted small mb-0">저장은 비동기로 처리됩니다.</p></div>
        <div class="card-body">
            <div id="boardMessage" class="alert d-none"></div>
            <form id="boardForm" class="row g-3" enctype="multipart/form-data">
                <div class="col-md-6"><label class="form-label" for="writer">작성자</label><input class="form-control" id="writer" name="writer" required></div>
                <div class="col-md-6"><label class="form-label" for="password">비밀번호</label><input type="password" autocomplete="current-password" class="form-control" id="password" name="password" required></div>
                <div class="col-12"><label class="form-label" for="title">제목</label><input class="form-control" id="title" name="title" required></div>
                <div class="col-12">
                    <label class="form-label" for="content">내용</label>
                    <div class="emoticon-toolbar mb-2" aria-label="이모티콘 선택">
                        <button class="emoticon-button" type="button" data-emoticon="😀" aria-label="smile">😀</button>
                        <button class="emoticon-button" type="button" data-emoticon="😂" aria-label="laugh">😂</button>
                        <button class="emoticon-button" type="button" data-emoticon="😍" aria-label="love">😍</button>
                        <button class="emoticon-button" type="button" data-emoticon="👍" aria-label="thumbs up">👍</button>
                        <button class="emoticon-button" type="button" data-emoticon="👏" aria-label="clap">👏</button>
                        <button class="emoticon-button" type="button" data-emoticon="🙏" aria-label="thanks">🙏</button>
                        <button class="emoticon-button" type="button" data-emoticon="🔥" aria-label="hot">🔥</button>
                        <button class="emoticon-button" type="button" data-emoticon="🎉" aria-label="party">🎉</button>
                        <button class="emoticon-button" type="button" data-emoticon="💯" aria-label="perfect">💯</button>
                        <button class="emoticon-button" type="button" data-emoticon="☕" aria-label="coffee">☕</button>
                    </div>
                    <textarea class="form-control" id="content" name="content" rows="8" required></textarea>
                    <div class="form-text">이모티콘을 누르면 현재 커서 위치에 삽입됩니다.</div>
                </div>
                <div class="col-12">
                    <label class="form-label" for="mediaFiles">사진 / 동영상 업로드</label>
                    <input type="file" class="form-control" id="mediaFiles" name="mediaFiles" accept="image/*,video/*" multiple>
                    <div class="form-text">JPG, PNG, GIF, MP4 등 이미지와 동영상 파일을 여러 개 선택할 수 있습니다.</div>
                    <div id="mediaPreview" class="media-preview mt-3"></div>
                </div>
                <div class="col-12 d-flex gap-2"><button class="btn btn-primary" type="submit">저장</button><a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/board/list.do">목록</a></div>
            </form>
        </div>
    </section>
</main>
<script src="${pageContext.request.contextPath}/js/board.js?v=20260609editmedia2"></script>
<%@ include file="/include/footer.jsp" %>





