document.addEventListener('DOMContentLoaded', function () {

    let myChart = null;
    let currentRange = 7;
    const chartElement = document.getElementById('productivityChart');


    const isDark = () => document.documentElement.getAttribute('data-theme') === 'dark';

    const getChartColors = () => {
        const dark = isDark();
        return {
            border: dark ? '#3b82f6' : '#001f3f',
            point: '#007bff',
            grid: dark ? 'rgba(255, 255, 255, 0.05)' : 'rgba(0, 0, 0, 0.05)',
            text: dark ? '#94a3b8' : '#64748b'
        };
    };


    function initChart(labels, values) {
        const targetCanvas = document.getElementById('productivityChart');
        if (!targetCanvas) return;

        if (myChart) {
            myChart.destroy();
        }

        const ctx = targetCanvas.getContext('2d');
        const colors = getChartColors();
        const chartHeight = targetCanvas.clientHeight || 400;
        const gradient = ctx.createLinearGradient(0, 0, 0, chartHeight);

        if (isDark()) {
            gradient.addColorStop(0, 'rgba(59, 130, 246, 0.4)');
            gradient.addColorStop(1, 'rgba(30, 41, 59, 0)');
        } else {
            gradient.addColorStop(0, 'rgba(0, 31, 63, 0.2)');
            gradient.addColorStop(1, 'rgba(255, 255, 255, 0)');
        }

        myChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Tasks Completed',
                    data: values,
                    fill: true,
                    backgroundColor: gradient,
                    borderColor: colors.border,
                    borderWidth: 3,
                    tension: 0.4,
                    pointBackgroundColor: colors.point,
                    pointBorderColor: isDark() ? '#1e293b' : '#fff',
                    pointBorderWidth: 2,
                    pointRadius: 4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { display: false } },
                scales: {
                    x: { grid: { display: false }, ticks: { color: colors.text } },
                    y: {
                        beginAtZero: true,
                        suggestedMax: 5,
                        ticks: { stepSize: 1, color: colors.text },
                        grid: { color: colors.grid }
                    }
                }
            }
        });
    }


    if (chartElement) {
        const initialLabels = JSON.parse(chartElement.getAttribute('data-labels') || '[]');
        const initialValues = JSON.parse(chartElement.getAttribute('data-values') || '[]');
        initChart(initialLabels, initialValues);
    }


    const sidebar = document.getElementById('sidebar');
    const sidebarBtn = document.getElementById('sidebarCollapse');
    const sidebarIcon = sidebarBtn?.querySelector('i');

    if (sidebar && sidebarBtn) {
        if (localStorage.getItem('sidebar-collapsed') === 'true') {
            sidebar.classList.add('collapsed');
            sidebarIcon?.classList.replace('fa-angles-left', 'fa-angles-right');
        }

        sidebarBtn.addEventListener('click', () => {
            sidebar.classList.toggle('collapsed');
            const isCollapsed = sidebar.classList.contains('collapsed');

            sidebarIcon?.classList.replace(
                isCollapsed ? 'fa-angles-left' : 'fa-angles-right',
                isCollapsed ? 'fa-angles-right' : 'fa-angles-left'
            );
            localStorage.setItem('sidebar-collapsed', isCollapsed);
            setTimeout(() => { if(myChart) myChart.resize(); }, 300);
        });
    }


    window.updateChartRange = function(days, labelText) {
        currentRange = days;
        const btnText = document.getElementById('selectedRangeText');
        if(btnText) btnText.innerText = labelText;

        fetch(`/api/tasks/stats/trend?days=${days}`)
            .then(res => res.json())
            .then(data => initChart(data.labels, data.values))
            .catch(err => console.error("Update failed:", err));
    };

    window.refreshCurrentChart = function() {
        const label = document.getElementById('selectedRangeText')?.innerText || 'Last 7 Days';
        window.updateChartRange(currentRange, label);
    };
});