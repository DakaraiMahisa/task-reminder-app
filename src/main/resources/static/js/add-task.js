
async function openEditModal(id) {
    try {
        const response = await fetch(`/api/v1/tasks/${id}`);
        if (!response.ok) throw new Error("Could not retrieve task data");
        const task = await response.json();


        document.getElementById('taskId').value = task.id;
        document.getElementById('taskTitle').value = task.title;
        document.getElementById('taskDesc').value = task.description;
        document.getElementById('taskDueDate').value = task.dueDate;
        document.getElementById('taskStatus').value = task.status;
        document.getElementById('taskPriority').value = task.priority;

        document.getElementById('modalTitle').innerText = "Edit Task";
        document.querySelector('#taskForm .btn-primary').innerHTML = '<i class="fa-solid fa-floppy-disk"></i> Update Task';


        const modalElement = document.getElementById('addTaskModal');
        const myModal = new bootstrap.Modal(modalElement);
        myModal.show();
    } catch (error) {
        console.error("Edit Error:", error);
    }
}

async function openViewModal(id) {
    try {
        const response = await fetch(`/api/v1/tasks/${id}`);
        const task = await response.json();


        document.getElementById('viewTitle').innerText = task.title;
        document.getElementById('viewDesc').innerText = task.description || "No description provided.";
        document.getElementById('viewDate').innerText = new Date(task.dueDate).toLocaleString();


        const pColor = task.priority === 'HIGH' ? 'text-danger' : (task.priority === 'MEDIUM' ? 'text-warning' : 'text-success');
        document.getElementById('viewPriority').innerHTML = `<i class="fa-solid fa-flag ${pColor}"></i> ${task.priority}`;


        const sClass = task.status === 'COMPLETED' ? 'bg-success' : (task.status === 'IN_PROGRESS' ? 'bg-info text-dark' : 'bg-secondary');
        document.getElementById('viewStatusBadge').innerHTML = `<span class="badge badge-custom ${sClass}">${task.status}</span>`;

        const myModal = new bootstrap.Modal(document.getElementById('viewTaskModal'));
        myModal.show();
    } catch (error) {
        console.error("View Modal Error:", error);
    }
}
document.addEventListener('DOMContentLoaded', function() {
document.getElementById('taskForm').addEventListener('submit', async function(e) {
    e.preventDefault();

    const id = document.getElementById('taskId').value;

    const isEdit = id !== "";


    const url = isEdit ? `/api/v1/tasks/update/${id}` : '/api/v1/tasks/add';
    const method = isEdit ? 'PUT' : 'POST';

    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    const payload = {
        title: document.getElementById('taskTitle').value,
        description: document.getElementById('taskDesc').value,
        dueDate: document.getElementById('taskDueDate').value,
        status: document.getElementById('taskStatus').value,
        priority: document.getElementById('taskPriority').value
    };

    try {

        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
                [header]: token
            },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            window.location.reload();
        } else {
            const errorData = await response.text();
            alert("Server says: " + errorData);
        }
    } catch (error) {
        console.error('Connection Error:', error);
    }
});

const addTaskModal = document.getElementById('addTaskModal');
    if (addTaskModal) {
        addTaskModal.addEventListener('hidden.bs.modal', function () {
            console.log("Modal closed - resetting form fields.");
            document.getElementById('taskForm').reset();
        });
    }


   document.querySelector('[data-bs-target="#addTaskModal"]').addEventListener('click', function() {
       document.getElementById('taskId').value = '';
       document.getElementById('taskForm').reset();
       document.getElementById('modalTitle').innerText = "Add New Task";
       document.querySelector('#taskForm .btn-primary').innerHTML = '<i class="fa-solid fa-floppy-disk"></i> Save Task';
   });
});
