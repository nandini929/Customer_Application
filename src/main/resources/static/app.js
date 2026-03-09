const apiBase = '/api/customers';

const form = document.getElementById('customer-form');
const submitBtn = document.getElementById('submit-btn');
const submitText = document.getElementById('submit-text');
const submitSpinner = document.getElementById('submit-spinner');
const formMessage = document.getElementById('form-message');
const formSuccess = document.getElementById('form-success');

// Configure date picker limits (only past dates allowed) and open calendar on click
const dobInput = document.getElementById('dateOfBirth');
if (dobInput) {
    const today = new Date();
    const maxDate = new Date(today);
    maxDate.setDate(maxDate.getDate() - 1); // yesterday
    const maxY = maxDate.getFullYear();
    const maxM = String(maxDate.getMonth() + 1).padStart(2, '0');
    const maxD = String(maxDate.getDate()).padStart(2, '0');

    dobInput.min = '1900-01-01';
    dobInput.max = `${maxY}-${maxM}-${maxD}`;

    dobInput.addEventListener('click', () => {
        if (dobInput.showPicker) {
            dobInput.showPicker();
        }
    });
}

function setSubmitting(isSubmitting) {
    submitBtn.disabled = isSubmitting;
    submitSpinner.style.display = isSubmitting ? 'inline-block' : 'none';
    submitText.textContent = isSubmitting ? 'Creating...' : 'Create customer';
}

function showError(message) {
    formMessage.textContent = message;
    formMessage.style.display = 'block';
    formSuccess.style.display = 'none';
}

function showSuccess(message) {
    formSuccess.textContent = message;
    formSuccess.style.display = 'block';
    formMessage.style.display = 'none';
}

form.addEventListener('submit', async (e) => {
    e.preventDefault();
    formMessage.style.display = 'none';
    formSuccess.style.display = 'none';

    const firstName = form.firstName.value.trim();
    const lastName = form.lastName.value.trim();
    const dateOfBirth = form.dateOfBirth.value;

    if (!firstName || !lastName || !dateOfBirth) {
        showError('Please fill in all fields.');
        return;
    }

    // Frontend validation for date of birth: must be a valid past date
    const dobDate = new Date(dateOfBirth);
    if (Number.isNaN(dobDate.getTime())) {
        showError('Date of birth is not a valid date.');
        return;
    }
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    if (dobDate >= today) {
        showError('Date of birth must be in the past.');
        return;
    }

    setSubmitting(true);

    try {
        const res = await fetch(apiBase, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ firstName, lastName, dateOfBirth })
        });

        if (res.status === 400) {
            const body = await res.json();
            const errors = body.errors || {};
            const firstError = Object.values(errors)[0] || body.message || 'Validation failed.';
            showError(firstError);
        } else if (!res.ok) {
            showError('Failed to create customer. Status ' + res.status);
        } else {
            const created = await res.json();
            showSuccess('Customer #' + created.id + ' created.');
            form.reset();
        }
    } catch (err) {
        console.error(err);
        showError('Network error. Is the backend running on port 8080?');
    } finally {
        setSubmitting(false);
    }
});

