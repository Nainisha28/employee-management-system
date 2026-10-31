
const $ = id => document.getElementById(id);

let token = localStorage.getItem('emsToken') || '';
let employees = [];
let page = 0;
let pageSize = 10;
let totalPages = 1;
let sortBy = 'employeeCode';
let direction = 'asc';
let debounce;

const departments = ['IT', 'HR', 'Finance', 'Sales', 'Admin'];

function authHeaders(json = true) {
    const h = {};

    if (token) {
        h.Authorization = 'Bearer ' + token;
    }

    if (json) {
        h['Content-Type'] = 'application/json';
    }

    return h;
}

async function api(url, options = {}) {
    const r = await fetch(url, {
        ...options,
        headers: {
            ...authHeaders(options.body !== undefined),
            ...(options.headers || {})
        }
    });

    if (r.status === 401) {
        logout();
        throw Error('Session expired. Please sign in again.');
    }

    if (r.status === 204) {
        return null;
    }

    let data;

    try {
        data = await r.json();
    } catch {
        data = {};
    }

    if (!r.ok) {
        throw Error(
            data.error ||
            data.message ||
            'Request failed (' + r.status + ')'
        );
    }

    return data;
}

function toast(msg, bad = false) {
    $('toast').textContent = msg;
    $('toast').classList.remove('hidden', 'bad');

    if (bad) {
        $('toast').classList.add('bad');
    }

    setTimeout(() => {
        $('toast').classList.add('hidden');
    }, 3200);
}

function showApp() {
    $('loginScreen').classList.add('hidden');
    $('app').classList.remove('hidden');

    loadEmployees();
    loadEmployeeOptions();
}

function logout() {
    token = '';

    localStorage.removeItem('emsToken');

    $('app').classList.add('hidden');
    $('loginScreen').classList.remove('hidden');
}

// Login form
$('loginForm').addEventListener('submit', async e => {
    e.preventDefault();
    $('loginError').textContent = '';

    try {
        const d = await api('/api/auth/login', {
            method: 'POST',
            body: JSON.stringify({
                username: $('username').value,
                password: $('password').value
            })
        });

        token = d.token;
        localStorage.setItem('emsToken', token);

        showApp();
    } catch (err) {
        $('loginError').textContent = err.message;
    }
});

$('logoutBtn').onclick = logout;
// Show / Hide password
$('togglePassword').addEventListener('click', () => {
    const password = $('password');
    const toggle = $('togglePassword');

    if (password.type === 'password') {
        password.type = 'text';
        toggle.textContent = '🙈';
    } else {
        password.type = 'password';
        toggle.textContent = '👁';
    }
});

// Build employee search query
function query() {
    const p = new URLSearchParams();

    p.set('page', page);
    p.set('size', pageSize);
    p.set('sortBy', sortBy);
    p.set('direction', direction);

    const filters = [
        ['search', 'search'],
        ['departmentFilter', 'department'],
        ['roleFilter', 'role'],
        ['statusFilter', 'status'],
        ['genderFilter', 'gender'],
        ['joiningFrom', 'joiningFrom'],
        ['joiningTo', 'joiningTo']
    ];

    for (const [id, key] of filters) {
        if ($(id) && $(id).value) {
            p.set(key, $(id).value);
        }
    }

    const salary = $('salaryFilter').value;

    if (salary === 'custom') {
        if ($('minSalary').value) {
            p.set('minSalary', $('minSalary').value);
        }

        if ($('maxSalary').value) {
            p.set('maxSalary', $('maxSalary').value);
        }
    } else if (salary) {
        const [min, max] = salary.split(',');

        if (min) p.set('minSalary', min);
        if (max) p.set('maxSalary', max);
    }

    return p.toString();
}

// Load employees from backend
async function loadEmployees() {
    try {
        const d = await api('/api/employees?' + query());

        employees = d.content || [];
        totalPages = d.totalPages || 1;

        renderRows();

        $('resultCount').textContent =
            `${d.totalElements || 0} matching employee${d.totalElements === 1 ? '' : 's'}`;

        $('pageLabel').textContent =
            `${Math.min(page + 1, totalPages)} / ${totalPages}`;

        $('prevPage').disabled = page <= 0;
        $('nextPage').disabled = page >= totalPages - 1;

        $('totalStat').textContent = d.totalElements || 0;

        $('activeStat').textContent =
            employees.filter(e => e.status === 'Active').length;

        $('avgStat').textContent = employees.length
            ? '₹' + Math.round(
                employees.reduce(
                    (s, e) => s + Number(e.annualSalary || 0),
                    0
                ) / employees.length
            ).toLocaleString('en-IN')
            : '—';

    } catch (e) {
        $('employeeRows').innerHTML = `
            <tr>
                <td colspan="7" class="empty">
                    ${escapeHtml(e.message)}.
                    Check that the backend and MySQL are running.
                </td>
            </tr>
        `;
    }
}

// Utility functions
function escapeHtml(s) {
    return String(s ?? '').replace(/[&<>"']/g, c => ({
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;'
    }[c]));
}

function initials(n) {
    return String(n || '?')
        .split(' ')
        .map(x => x[0])
        .slice(0, 2)
        .join('')
        .toUpperCase();
}

function money(n) {
    return '₹' + Number(n || 0).toLocaleString('en-IN', {
        maximumFractionDigits: 0
    });
}

// Render employee table
function renderRows() {
    if (!employees.length) {
        $('employeeRows').innerHTML = `
            <tr>
                <td colspan="7" class="empty">
                    No employees found. Try changing your filters or add an employee.
                </td>
            </tr>
        `;

        return;
    }

    $('employeeRows').innerHTML = employees.map(e => `
        <tr>
            <td>
                <div class="person">
                    <div class="person-avatar">
                        ${escapeHtml(initials(e.fullName))}
                    </div>

                    <div>
                        <div class="person-name">
                            ${escapeHtml(e.fullName)}
                        </div>

                        <div class="person-code">
                            <a href="#" data-action="view" data-id="${e.id}">
                                ${escapeHtml(e.employeeCode)}
                            </a>
                            · ID ${e.id}
                        </div>
                    </div>
                </div>
            </td>

            <td>
                <span class="dept">
                    ${escapeHtml(e.department)}
                </span>
            </td>

            <td>
                ${escapeHtml(e.roleName)}
                <span class="subtext">
                    Joined ${escapeHtml(e.joiningDate || '—')}
                </span>
            </td>

            <td>
                ${escapeHtml(e.email)}
                <span class="subtext">
                    ${escapeHtml(e.mobile)}
                </span>
            </td>

            <td>
                <b>${money(e.annualSalary)}</b>
            </td>

            <td>
                <span class="status ${escapeHtml(e.status)}">
                    ${escapeHtml(e.status)}
                </span>
            </td>

            <td>
                
            <div class="actions">
               <button class="icon-btn"
               title="View details"
               data-action="view"
               data-id="${e.id}">◉</button>

               <button class="icon-btn"
               title="Edit"
               data-action="edit"
               data-id="${e.id}">✎</button>

               <button class="icon-btn"
               title="Deactivate"
               data-action="deactivate"
               data-id="${e.id}">⊘</button>


            </div>

            </td>
        </tr>
    `).join('');
}

// Employee table actions
$('employeeRows').addEventListener('click', async e => {
    const b = e.target.closest('[data-action]');

    if (!b) return;

    e.preventDefault();

    const id = b.dataset.id;

    try {
        if (b.dataset.action === 'view') {
            await showDetails(id);
        }

        if (b.dataset.action === 'edit') {
            await editEmployee(id);
        }

        if (b.dataset.action === 'deactivate') {
            if (confirm('Deactivate this employee?')) {
                await api('/api/employees/' + id, {
                    method: 'DELETE'
                });

                toast('Employee deactivated');

                await loadEmployees();
                await loadEmployeeOptions();
            }
        }
        

    } catch (err) {
        toast(err.message, true);
    }
});

// Filters
const filterIds = [
    'search',
    'departmentFilter',
    'roleFilter',
    'statusFilter',
    'genderFilter',
    'joiningFrom',
    'joiningTo',
    'salaryFilter',
    'minSalary',
    'maxSalary'
];

filterIds.forEach(id => {
    $(id).addEventListener('input', () => {
        page = 0;

        clearTimeout(debounce);

        debounce = setTimeout(loadEmployees, 250);
    });
});

$('salaryFilter').onchange = () => {
    const custom = $('salaryFilter').value === 'custom';

    $('minSalary').classList.toggle('hidden', !custom);
    $('maxSalary').classList.toggle('hidden', !custom);

    page = 0;
    loadEmployees();
};

// Pagination
$('pageSize').onchange = () => {
    pageSize = Number($('pageSize').value);
    page = 0;

    loadEmployees();
};

$('prevPage').onclick = () => {
    if (page > 0) {
        page--;
        loadEmployees();
    }
};

$('nextPage').onclick = () => {
    if (page < totalPages - 1) {
        page++;
        loadEmployees();
    }
};

// Sorting
document.querySelectorAll('[data-sort]').forEach(b => {
    b.onclick = () => {
        direction =
            sortBy === b.dataset.sort && direction === 'asc'
                ? 'desc'
                : 'asc';

        sortBy = b.dataset.sort;

        loadEmployees();
    };
});

// Reset filters
$('resetBtn').onclick = () => {
    filterIds.forEach(id => {
        $(id).value = '';
    });

    $('minSalary').classList.add('hidden');
    $('maxSalary').classList.add('hidden');

    page = 0;
    loadEmployees();
};

$('moreFilters').onclick = () => {
    $('advancedFilters').classList.toggle('hidden');
};

// Employee modal
function openEmployeeModal() {
    $('employeeForm').reset();
    $('employeeId').value = '';
    $('modalTitle').textContent = 'Add employee';
    $('formError').textContent = '';

    $('employeeModal').classList.remove('hidden');

    $('dateOfBirth').max = new Date().toISOString().slice(0, 10);
    $('joiningDate').max = new Date().toISOString().slice(0, 10);
}

$('addBtn').onclick = openEmployeeModal;

function closeEmployeeModal() {
    $('employeeModal').classList.add('hidden');
}

$('closeModal').onclick =
    $('cancelModal').onclick = closeEmployeeModal;

// Aadhaar, PAN and mobile input formatting
$('aadhaar').addEventListener('input', e => {
    let n = e.target.value.replace(/\D/g, '').slice(0, 12);

    e.target.value = n.match(/.{1,4}/g)?.join('-') || '';
});

$('pan').addEventListener('input', e => {
    e.target.value = e.target.value
        .toUpperCase()
        .replace(/[^A-Z0-9]/g, '')
        .slice(0, 10);
});

$('mobile').addEventListener('input', e => {
    e.target.value = e.target.value
        .replace(/\D/g, '')
        .slice(0, 10);
});

// Validate and prepare employee form data
function formPayload() {
    const aadhaar = $('aadhaar').value.replace(/\D/g, '');

    if (aadhaar.length !== 12) {
        throw Error('Aadhaar must contain exactly 12 digits');
    }

    if (!/^[A-Z]{5}[0-9]{4}[A-Z]$/.test($('pan').value)) {
        throw Error('PAN must match ABCDE1234F');
    }

    if ($('dateOfBirth').value > new Date().toISOString().slice(0, 10)) {
        throw Error('Date of birth cannot be in the future');
    }

    if (Number($('annualSalary').value) <= 0) {
        throw Error('Salary must be greater than zero');
    }

    return {
        fullName: $('fullName').value.trim(),
        email: $('email').value.trim(),
        mobile: $('mobile').value,
        aadhaar,
        pan: $('pan').value,
        dateOfBirth: $('dateOfBirth').value,
        gender: $('gender').value,
        address: $('address').value.trim(),
        department: $('department').value,
        roleName: $('roleName').value,
        joiningDate: $('joiningDate').value,
        annualSalary: Number($('annualSalary').value),
        status: $('status').value
    };
}

// Create or update employee
$('employeeForm').onsubmit = async e => {
    e.preventDefault();

    $('formError').textContent = '';

    try {
        const payload = formPayload();
        const id = $('employeeId').value;

        await api(
            id ? '/api/employees/' + id : '/api/employees',
            {
                method: id ? 'PUT' : 'POST',
                body: JSON.stringify(payload)
            }
        );

        closeEmployeeModal();

        toast(id ? 'Employee updated' : 'Employee created');

        await loadEmployees();
        await loadEmployeeOptions();

    } catch (err) {
        $('formError').textContent = err.message;
    }
};

// Edit employee
async function editEmployee(id) {
    const e = await api('/api/employees/' + id);

    openEmployeeModal();

    $('modalTitle').textContent = 'Edit employee';
    $('employeeId').value = e.id;

    for (const key of [
        'fullName',
        'email',
        'mobile',
        'pan',
        'dateOfBirth',
        'gender',
        'address',
        'department',
        'roleName',
        'joiningDate',
        'annualSalary',
        'status'
    ]) {
        $(key).value = e[key] ?? '';
    }

    $('aadhaar').value = (e.aadhaar || '')
        .replace(/(\d{4})(?=\d)/g, '$1-');
}

// View employee details
async function showDetails(id) {
    const e = await api('/api/employees/' + id);

    $('detailsTitle').textContent =
        e.fullName + ' · ' + e.employeeCode;

    const fields = [
        ['Employee code', e.employeeCode],
        ['Full name', e.fullName],
        ['Email', e.email],
        ['Mobile', e.mobile],
        ['Aadhaar', e.aadhaar],
        ['PAN', e.pan],
        ['Date of birth', e.dateOfBirth],
        ['Gender', e.gender],
        ['Address', e.address],
        ['Department', e.department],
        ['Role', e.roleName],
        ['Joining date', e.joiningDate],
        ['Annual salary', money(e.annualSalary)],
        ['Status', e.status],
        ['Internal ID', e.id]
    ];

    $('detailsContent').innerHTML = fields.map(([k, v]) => `
        <div class="detail">
            <span>${escapeHtml(k)}</span>
            <b>${escapeHtml(v || '—')}</b>
        </div>
    `).join('');

    $('detailsModal').classList.remove('hidden');
}

$('closeDetails').onclick =
    $('detailsDone').onclick = () => {
        $('detailsModal').classList.add('hidden');
    };

// Employee options for department transfers
async function loadEmployeeOptions() {
    try {
        const d = await api(
            '/api/employees?page=0&size=20&sortBy=fullName&direction=asc'
        );

        const old = $('transferEmployee').value;

        $('transferEmployee').innerHTML =
            '<option value="">Choose employee</option>' +
            (d.content || []).map(e => `
                <option value="${e.id}">
                    ${escapeHtml(e.fullName)} —
                    ${escapeHtml(e.employeeCode)}
                    (${escapeHtml(e.department)})
                </option>
            `).join('');

        $('transferEmployee').value = old;

    } catch (e) {
        // Ignore employee-option loading errors.
    }
}

// Navigation
document.querySelectorAll('.nav').forEach(b => {
    b.onclick = () => {
        document.querySelectorAll('.nav').forEach(n => {
            n.classList.remove('active');
        });

        b.classList.add('active');

        const transfer = b.dataset.page === 'transfers';

        $('employeePage').classList.toggle('hidden', transfer);
        $('transferPage').classList.toggle('hidden', !transfer);
        $('addBtn').classList.toggle('hidden', transfer);

        $('pageTitle').textContent = transfer
            ? 'Department transfer'
            : 'Employee directory';

        $('crumb').textContent = transfer
            ? 'Department transfer'
            : 'Employees';
    };
});

// Department transfer
$('transferDate').value = new Date().toISOString().slice(0, 10);

$('transferForm').onsubmit = async e => {
    e.preventDefault();

    const id = $('transferEmployee').value;

    if (!id) return;

    try {
        const result = await api(
            '/api/employees/' + id + '/transfer',
            {
                method: 'POST',
                body: JSON.stringify({
                    newDepartment: $('newDepartment').value,
                    transferDate: $('transferDate').value,
                    reason: $('transferReason').value
                })
            }
        );

        $('transferResult').innerHTML = `
            <div class="detail" style="margin-top:18px">
                <b>Transfer completed</b>
                <p>
                    ${escapeHtml(result.fullName)} now belongs to
                    ${escapeHtml(result.department)} with new code
                    <strong>${escapeHtml(result.employeeCode)}</strong>.
                </p>
            </div>
        `;

        toast('Transfer completed successfully');

        $('transferForm').reset();
        $('transferDate').value = new Date().toISOString().slice(0, 10);

        await loadEmployees();
        await loadEmployeeOptions();

    } catch (err) {
        $('transferResult').innerHTML = `
            <p class="error">${escapeHtml(err.message)}</p>
        `;
    }
};

// Restore session if a token exists
if (token) {
    showApp();
}
