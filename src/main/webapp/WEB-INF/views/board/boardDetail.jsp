<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/include/header.jsp" %>
<main class="container py-4">
    <section class="card border-0 shadow-sm mb-4">
        <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <h2 class="h5 mb-0">게시글 상세</h2>
            <a class="btn btn-outline-secondary btn-sm" href="${pageContext.request.contextPath}/board/list.do">목록</a>
        </div>
        <div class="card-body">
            <div id="boardDetail" data-board-id="${param.boardId}">불러오는 중...</div>
            <div class="d-flex gap-2 mt-4">
                <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/board/edit.do?boardId=${param.boardId}">수정</a>
                <button class="btn btn-outline-danger" type="button" id="deleteBoardButton">삭제</button>
                <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/board/list.do">목록</a>
            </div>
            <div id="boardMessage" class="alert d-none mt-3"></div>
        </div>
    </section>

    <section class="card border-0 shadow-sm mb-4">
        <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <h3 class="h6 mb-0">댓글 목록</h3>
        </div>
        <div class="card-body">
            <div id="commentMessage" class="alert d-none"></div>
            <div id="commentList" data-board-id="${param.boardId}">
                <p class="text-muted mb-0">댓글을 불러오는 중...</p>
            </div>
        </div>
    </section>

    <section class="card border-0 shadow-sm">
        <div class="card-header bg-white"><h3 class="h6 mb-0">댓글 작성</h3></div>
        <div class="card-body">
            <form id="commentForm" class="row g-3">
                <input type="hidden" name="boardId" value="${param.boardId}">
                <div class="col-md-4"><label class="form-label" for="commentWriter">작성자</label><input class="form-control" id="commentWriter" name="writer" required></div>
                <div class="col-md-4"><label class="form-label" for="commentPassword">비밀번호</label><input type="password" autocomplete="current-password" class="form-control" id="commentPassword" name="password" required></div>
                <div class="col-12"><label class="form-label" for="commentContent">내용</label><textarea class="form-control" id="commentContent" name="content" rows="4" required></textarea></div>
                <div class="col-12"><button class="btn btn-primary" type="submit">댓글 등록</button></div>
            </form>
        </div>
    </section>
</main>
<script src="${pageContext.request.contextPath}/js/board.js?v=20260609editmedia2"></script>
<%@ include file="/include/footer.jsp" %>




