const apiBase = '/api/customers';

const customersEmpty = document.getElementById('customers-empty');
const customersTable = document.getElementById('customers-table');
const customersBody = document.getElementById('customers-body');
const countPill = document.getElementById('count-pill');

async function fetchCustomers() {
    try {
        const res = await fetch(apiBase);
        if (!res.ok) {
            throw new Error('Failed to load customers');
        }
        const data = await res.json();
        renderCustomers(data);
    } catch (e) {
        console.error(e);
    }
}

function renderCustomers(customers) {
    customersBody.innerHTML = '';
    if (!customers || customers.length === 0) {
        customersEmpty.style.display = 'block';
        customersTable.style.display = 'none';
        countPill.textContent = '0 customers';
        return;
    }

    customersEmpty.style.display = 'none';
    customersTable.style.display = 'table';

    customers.forEach(c => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
                <td>${c.id}</td>
                <td>${c.firstName}</td>
                <td>${c.lastName}</td>
                <td>${c.dateOfBirth}</td>
            `;
        customersBody.appendChild(tr);
    });
    countPill.textContent = customers.length + (customers.length === 1 ? ' customer' : ' customers');
}

// Initial load
fetchCustomers();

