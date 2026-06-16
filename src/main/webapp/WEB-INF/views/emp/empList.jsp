<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/include/header.jsp" %>
<main class="container py-4">
    <section class="card border-0 shadow-sm mb-4">
        <div class="card-header bg-white">
            <h2 class="h5 mb-1">사원 등록 / 수정</h2>
            <p class="text-muted small mb-0">사원 데이터 처리는 비동기로 실행됩니다.</p>
        </div>
        <div class="card-body">
            <form id="empForm" class="row g-3">
                <input type="hidden" id="empNo" name="empNo">
                <div class="col-md-3">
                    <label for="empName" class="form-label">이름</label>
                    <input type="text" class="form-control" id="empName" name="empName" required>
                </div>
                <div class="col-md-3">
                    <label for="deptName" class="form-label">부서번호</label>
                    <input type="number" class="form-control" id="deptName" name="deptName" placeholder="10, 20, 30, 40" required>
                </div>
                <div class="col-md-3">
                    <label for="position" class="form-label">직급</label>
                    <input type="text" class="form-control" id="position" name="position" required>
                </div>
                <div class="col-md-3">
                    <label for="salary" class="form-label">급여</label>
                    <input type="number" class="form-control" id="salary" name="salary" min="0" required>
                </div>
                <div class="col-12 d-flex gap-2">
                    <button type="submit" class="btn btn-primary" id="saveButton">등록</button>
                    <button type="button" class="btn btn-outline-secondary" id="resetButton">초기화</button>
                </div>
            </form>
        </div>
    </section>

    <section class="card border-0 shadow-sm">
        <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <div>
                <h2 class="h5 mb-1">전체 사원 목록</h2>
                <p class="text-muted small mb-0">모든 사원을 비동기로 조회합니다.</p>
            </div>
            <button class="btn btn-outline-primary" type="button" id="reloadButton">새로고침</button>
        </div>
        <div class="card-body">
            <div id="empMessage" class="alert d-none"></div>
            <div class="table-responsive">
                <table class="table align-middle">
                    <thead>
                    <tr>
                        <th>사번</th>
                        <th>이름</th>
                        <th>부서번호</th>
                        <th>직급</th>
                        <th>급여</th>
                        <th>관리</th>
                    </tr>
                    </thead>
                    <tbody id="empTableBody">
                    <tr><td colspan="6" class="text-center text-muted py-4">불러오는 중...</td></tr>
                    </tbody>
                </table>
            </div>
        </div>
    </section>
</main>
<script src="${pageContext.request.contextPath}/js/emp.js?v=20260609refactor1"></script>
<%@ include file="/include/footer.jsp" %>