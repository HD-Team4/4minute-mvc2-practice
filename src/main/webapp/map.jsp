<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/include/header.jsp" %>
<main class="container py-4">
    <section class="card border-0 shadow-sm">
        <div class="card-header bg-white">
            <h2 class="h5 mb-1">회사 위치 지도</h2>
            <p class="text-muted small mb-0">카카오 지도에 회사 위치를 표시합니다.</p>
        </div>
        <div class="card-body">
            <div id="map" class="map-box" data-app-key="${kakaoMapAppKey}"></div>
            <div id="mapFallback" class="map-fallback d-none">
                <p class="mb-3">지도를 불러올 수 없습니다.</p>
                <a class="btn btn-warning btn-sm" target="_blank" rel="noopener" href="https://map.kakao.com/link/map/4Minute,37.4948802,127.1222790">카카오맵에서 위치 열기</a>
            </div>
        </div>
    </section>
</main>
<script src="${pageContext.request.contextPath}/js/map.js"></script>
<%@ include file="/include/footer.jsp" %>
