<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/include/header.jsp" %>
<main class="container py-4">
    <div class="row g-4">
        <section class="col-lg-5">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-header bg-white">
                    <div>
                        <h2 class="h5 mb-1">회사 기준 위치 현재 날씨</h2>
                    </div>
                </div>
                <div class="card-body">
                    <div id="weatherResult" class="weather-panel">
                    </div>
                </div>
            </div>
        </section>
        <section class="col-lg-7">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-header bg-white d-flex flex-wrap gap-3 justify-content-between align-items-center">
                    <div>
                        <h2 class="h5 mb-1">미래를 위한 퇴직연금 정보</h2>
                    </div>
                    <div class="btn-group pension-toggle" role="group" aria-label="retirement pension count">
                        <button class="btn btn-primary btn-sm active" type="button" data-pension-count="10">10개</button>
                        <button class="btn btn-outline-primary btn-sm" type="button" data-pension-count="20">20개</button>
                    </div>
                </div>
                <div class="card-body">
                    <div id="pensionResult" class="pension-panel">
                    </div>
                </div>
            </div>
        </section>
    </div>
</main>
<script src="${pageContext.request.contextPath}/js/api.js"></script>
<%@ include file="/include/footer.jsp" %>
