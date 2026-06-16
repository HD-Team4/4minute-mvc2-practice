let employeesCache = [];

document.addEventListener('DOMContentLoaded', () => {
    const form = document.querySelector('#empForm');
    const tableBody = document.querySelector('#empTableBody');

    if (!form || !tableBody) return;

    loadEmployees();
    form.addEventListener('submit', saveEmployee);
    tableBody.addEventListener('click', handleEmployeeAction);
    document.querySelector('#resetButton')?.addEventListener('click', resetEmpForm);
    document.querySelector('#reloadButton')?.addEventListener('click', loadEmployees);
});

async function loadEmployees() {
    const tableBody = document.querySelector('#empTableBody');

    try {
        const response = await fetch(`${getContextPath()}/emp/api/list`);
        if (!response.ok) {
            const result = await safeJson(response);
            throw new Error(result.message || '사원 조회 실패');
        }

        employeesCache = await response.json();
        renderEmployees(employeesCache);
    } catch (error) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="6" class="text-center text-danger py-4">${escapeHtml(error.message)}</td>
            </tr>
        `;
    }
}

async function saveEmployee(event) {
    event.preventDefault();

    try {
        const form = event.target;
        const formData = new FormData(form);
        const path = formData.get('empNo') ? '/emp/api/update' : '/emp/api/create';
        const result = await requestEmp(path, formData);

        showEmpMessage(result.message, result.success);
        if (result.success) {
            resetEmpForm();
            await loadEmployees();
        }
    } catch (error) {
        showEmpMessage(error.message, false);
    }
}

async function handleEmployeeAction(event) {
    const button = event.target.closest('button[data-action]');
    if (!button) return;

    const empNo = Number(button.dataset.empNo);

    try {
        if (button.dataset.action === 'edit') {
            const emp = employeesCache.find((item) => item.empNo === empNo);
            setEditMode(emp);
            return;
        }

        if (button.dataset.action === 'delete') {
            await deleteEmployee(empNo);
        }
    } catch (error) {
        showEmpMessage(error.message, false);
    }
}

async function deleteEmployee(empNo) {
    if (!confirm('선택한 사원을 삭제하시겠습니까?')) return;

    const formData = new FormData();
    formData.append('empNo', empNo);

    const result = await requestEmp('/emp/api/delete', formData);
    showEmpMessage(result.message, result.success);

    if (result.success) {
        await loadEmployees();
    }
}

async function requestEmp(path, formData) {
    const response = await fetch(`${getContextPath()}${path}`, {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'},
        body: new URLSearchParams(formData)
    });
    const result = await safeJson(response);

    if (!response.ok) {
        throw new Error(result.message || '요청 실패');
    }

    return result;
}

function renderEmployees(employees) {
    const tableBody = document.querySelector('#empTableBody');

    if (!employees.length) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="6" class="text-center text-muted py-4">등록된 사원이 없습니다.</td>
            </tr>
        `;
        return;
    }

    tableBody.innerHTML = employees.map((emp) => `
        <tr>
            <td>${emp.empNo}</td>
            <td>${escapeHtml(emp.empName)}</td>
            <td>${escapeHtml(emp.deptName)}</td>
            <td>${escapeHtml(emp.position)}</td>
            <td>${Number(emp.salary).toLocaleString()}</td>
            <td>
                <button type="button" class="btn btn-sm btn-outline-secondary" data-action="edit" data-emp-no="${emp.empNo}">수정</button>
                <button type="button" class="btn btn-sm btn-outline-danger" data-action="delete" data-emp-no="${emp.empNo}">삭제</button>
            </td>
        </tr>
    `).join('');
}

function setEditMode(emp) {
    if (!emp) return;

    document.querySelector('#empNo').value = emp.empNo;
    document.querySelector('#empName').value = emp.empName;
    document.querySelector('#deptName').value = emp.deptName;
    document.querySelector('#position').value = emp.position;
    document.querySelector('#salary').value = emp.salary;
    document.querySelector('#saveButton').textContent = '수정';
}

function resetEmpForm() {
    document.querySelector('#empForm').reset();
    document.querySelector('#empNo').value = '';
    document.querySelector('#saveButton').textContent = '등록';
}

function showEmpMessage(message, success) {
    const box = document.querySelector('#empMessage');
    box.textContent = message;
    box.className = `alert ${success ? 'alert-success' : 'alert-danger'}`;
}

async function safeJson(response) {
    try {
        return await response.json();
    } catch (error) {
        return {};
    }
}

function escapeHtml(value) {
    return String(value ?? '')
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#039;');
}

function getContextPath() {
    if (window.CONTEXT_PATH !== undefined) return window.CONTEXT_PATH;

    const path = window.location.pathname;
    const index = path.indexOf('/', 1);
    return index === -1 ? '' : path.substring(0, index);
}