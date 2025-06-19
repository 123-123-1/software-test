import matplotlib.pyplot as plt
import numpy as np
from datetime import datetime, timedelta
import matplotlib.dates as mdates

# 1. Font configuration
plt.rcParams['font.sans-serif'] = ['Microsoft YaHei']
plt.rcParams['axes.unicode_minus'] = False

# 2. Project configuration
project_start = datetime(2025, 3, 24)
project_end = datetime(2025, 6, 16)
total_days = (project_end - project_start).days
total_tasks = 30  # Reduced to 30 tasks

# 3. Generate smoother progress line
np.random.seed(42)

# Baseline plan line (smoother curve)
x = np.linspace(0, 1, total_days + 1)
ideal_line = total_tasks * (1 - x**1.1)  # Non-linear decrease

# Actual progress line (one turning point per week)
actual_progress = []
points_per_week = 7  # Number of data points per week
total_weeks = total_days // points_per_week + 1

# Generate more realistic progress changes
current_progress = total_tasks
for week in range(total_weeks):
    # Calculate tasks that should be completed this week (based on ideal progress)
    week_start_day = week * points_per_week
    week_end_day = min((week + 1) * points_per_week, total_days)
    
    if week_start_day < len(ideal_line) and week_end_day < len(ideal_line):
        # Calculate tasks that should be completed this week
        week_tasks = ideal_line[week_start_day] - ideal_line[week_end_day]
        
        # Add some randomness, but ensure no progress increase
        weekly_completion_rate = np.random.uniform(0.7, 1.3)  # Completion rate between 70%-130%
        actual_week_tasks = week_tasks * weekly_completion_rate
        
        # Ensure no progress increase
        actual_week_tasks = min(actual_week_tasks, current_progress)
        
        # Generate linear progress changes for this week
        for day in range(points_per_week):
            if week * points_per_week + day <= total_days:
                # Linear progress decrease
                daily_progress = current_progress - (actual_week_tasks * (day + 1) / points_per_week)
                actual_progress.append(max(0, daily_progress))
        
        # Update current progress
        current_progress = max(0, current_progress - actual_week_tasks)

# 4. Plot the chart
fig, ax = plt.subplots(figsize=(12, 6), facecolor='#F8F8F8')

# Baseline plan line
ax.plot([project_start + timedelta(days=i) for i in range(total_days + 1)],
        ideal_line, '--', color='#4E79A7', linewidth=2, alpha=0.8, label='Planned Progress')

# Actual progress line (straight segments)
ax.plot([project_start + timedelta(days=i) for i in range(len(actual_progress))],
        actual_progress, '-', color='#E15759', linewidth=2.5, label='Actual Progress',
        solid_capstyle='round')

# 5. Chart beautification
ax.set_title('Project Burndown Chart', fontsize=14, pad=20, fontweight='semibold')
ax.set_xlabel('Date', fontsize=11, labelpad=10)
ax.set_ylabel('Remaining Tasks', fontsize=11, labelpad=10)

# Optimize X-axis display
ax.xaxis.set_major_locator(mdates.WeekdayLocator(byweekday=mdates.MO, interval=2))
ax.xaxis.set_major_formatter(mdates.DateFormatter('%m/%d'))
plt.xticks(rotation=45, ha='right')

# Style adjustments
ax.grid(axis='y', linestyle=':', alpha=0.4, linewidth=0.8)
ax.spines['top'].set_visible(False)
ax.spines['right'].set_visible(False)
ax.legend(frameon=False, fontsize=10)

plt.tight_layout()
plt.show() 