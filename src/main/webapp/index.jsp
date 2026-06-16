<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/include/header.jsp" %>
<main class="container py-4">
    <div class="row g-4">
        <div class="col-md-6 col-xl-3">
            <a class="portal-tile emp-tile" href="${pageContext.request.contextPath}/emp/list.do">
                <span>EMP</span>
                <strong>사원 관리</strong>
                <small>등록 · 수정 · 삭제 · 전체조회</small>
            </a>
        </div>
        <div class="col-md-6 col-xl-3">
            <a class="portal-tile board-tile" href="${pageContext.request.contextPath}/board/list.do">
                <span>BOARD</span>
                <strong>사내 게시판</strong>
                <small>글 작성 · 상세 · 수정 · 삭제</small>
            </a>
        </div>
        <div class="col-md-6 col-xl-3">
            <a class="portal-tile api-tile" href="${pageContext.request.contextPath}/api/dashboard.do">
                <span>API</span>
                <strong>외부 API</strong>
                <small>날씨 · 퇴직연금 정보</small>
            </a>
        </div>
        <div class="col-md-6 col-xl-3">
            <a class="portal-tile map-tile" href="${pageContext.request.contextPath}/api/map.do">
                <span>MAP</span>
                <strong>지도</strong>
                <small>카카오 지도 위치 확인</small>
            </a>
        </div>
    </div>
</main>
<%@ include file="/include/footer.jsp" %>