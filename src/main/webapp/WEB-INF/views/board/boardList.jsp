<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/include/header.jsp" %>
<main class="container py-4">
    <section class="card border-0 shadow-sm">
        <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <div>
                <h2 class="h5 mb-1">사내 게시판</h2>
                <p class="text-muted small mb-0">목록에는 댓글 내용을 표시하지 않고 개수만 표시합니다.</p>
            </div>
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/board/write.do">글쓰기</a>
        </div>
        <div class="card-body">
            <div class="table-responsive">
                <table class="table align-middle">
                    <thead>
                    <tr>
                        <th>번호</th>
                        <th>제목</th>
                        <th>작성자</th>
                        <th>댓글</th>
                        <th>조회수</th>
                        <th>작성일</th>
                    </tr>
                    </thead>
                    <tbody id="boardTableBody">
                    <tr><td colspan="6" class="text-center text-muted py-4">불러오는 중...</td></tr>
                    </tbody>
                </table>
            </div>
        </div>
    </section>
</main>
<script src="${pageContext.request.contextPath}/js/board.js?v=20260609editmedia2"></script>
<%@ include file="/include/footer.jsp" %>



