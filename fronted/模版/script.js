// 队伍数据
const teams = [
    { name: '重庆先锋代表队', score: 0, answered: 0, correct: 0 },
    { name: '重庆智慧代表队', score: 0, answered: 0, correct: 0 },
    { name: '重庆创新代表队', score: 0, answered: 0, correct: 0 },
    { name: '重庆精英代表队', score: 0, answered: 0, correct: 0 },
    { name: '重庆挑战代表队', score: 0, answered: 0, correct: 0 },
    { name: '重庆梦想代表队', score: 0, answered: 0, correct: 0 },
    { name: '重庆奋斗代表队', score: 0, answered: 0, correct: 0 },
    { name: '重庆超越代表队', score: 0, answered: 0, correct: 0 },
    { name: '重庆荣耀代表队', score: 0, answered: 0, correct: 0 },
    { name: '重庆胜利代表队', score: 0, answered: 0, correct: 0 },
    { name: '重庆团结代表队', score: 0, answered: 0, correct: 0 },
    { name: '重庆拼搏代表队', score: 0, answered: 0, correct: 0 }
];

// 计算最优行列分布
function calculateOptimalLayout(totalItems) {
    if (totalItems <= 0) return { rows: 0, cols: 0 };
    
    // 计算最大列数（基于网页宽度和网格宽度）
    const maxCols = Math.floor(1600 / (280 + 20)); // 网格宽度 + 间距
    
    // 优先考虑更多的行数，避免一行队伍过多
    let bestLayout = { rows: 1, cols: Math.min(totalItems, maxCols), diff: 0 };
    
    // 从最大列数开始，逐渐减少列数，增加行数
    for (let cols = maxCols; cols >= 1; cols--) {
        const rows = Math.ceil(totalItems / cols);
        const remainder = totalItems % cols;
        
        // 计算最大和最小列数的差值
        const diff = remainder > 0 ? 1 : 0;
        
        // 优先选择行数更多的布局（即列数更少的布局）
        if (rows > bestLayout.rows || (rows === bestLayout.rows && diff < bestLayout.diff)) {
            bestLayout = { rows, cols, diff };
        }
        
        // 如果能够完美分布，优先选择
        if (diff === 0) {
            bestLayout = { rows, cols, diff: 0 };
            break;
        }
    }
    
    return bestLayout;
}

// 初始化网格
function initializeGrid() {
    const gridContainer = document.getElementById('scoreGrid');
    gridContainer.innerHTML = '';

    // 计算最优布局
    const layout = calculateOptimalLayout(teams.length);
    
    // 设置网格布局
    gridContainer.style.display = 'grid';
    gridContainer.style.gridTemplateColumns = `repeat(${layout.cols}, 280px)`;
    gridContainer.style.gridTemplateRows = `repeat(${layout.rows}, 200px)`;
    gridContainer.style.gap = '20px';
    gridContainer.style.justifyContent = 'center';
    gridContainer.style.alignItems = 'center';
    gridContainer.style.maxWidth = '1600px';
    gridContainer.style.width = '90%';
    gridContainer.style.height = 'auto';
    gridContainer.style.minHeight = '500px';

    teams.forEach((team, index) => {
        const card = document.createElement('div');
        card.className = 'score-card';
        card.innerHTML = `
            <div class="team-name">${team.name}</div>
            <div class="score" id="score-${index}">${team.score}</div>
            <div class="stats-container">
                <div class="accuracy" id="accuracy-${index}">正确率: 0%</div>
                <div class="answered-count" id="answered-${index}">已答: 0</div>
            </div>
        `;
        gridContainer.appendChild(card);
    });
}

// 动态添加新队伍
function addTeam(teamName) {
    const newTeam = {
        name: teamName,
        score: 0,
        answered: 0,
        correct: 0
    };
    teams.push(newTeam);
    
    // 重新初始化网格以应用新的布局
    initializeGrid();
    initializeTeamData();
}

// 动态移除队伍
function removeTeam(teamIndex) {
    if (teamIndex >= 0 && teamIndex < teams.length) {
        teams.splice(teamIndex, 1);
        // 重新初始化网格以应用新的布局
        initializeGrid();
        initializeTeamData();
    }
}

// 计算正确率
function calculateAccuracy(correct, answered) {
    if (answered === 0) return 0;
    return Math.round((correct / answered) * 100);
}

// 更新队伍数据（随机加分或减分）
function updateTeamData(teamIndex) {
    const team = teams[teamIndex];
    
    // 随机增加数据
    const newAnswered = team.answered + Math.floor(Math.random() * 3);
    // 确保正确数量不超过已答数量
    const maxPossibleCorrect = newAnswered - team.answered;
    const newCorrect = team.correct + Math.floor(Math.random() * (maxPossibleCorrect + 1));
    
    // 随机决定是加分还是减分（70%概率加分，30%概率减分）
    const isPositive = Math.random() < 0.7;
    const scoreChange = isPositive ? 10 : -10;
    const newScore = Math.max(0, team.score + scoreChange); // 确保分数不为负
    
    team.answered = newAnswered;
    team.correct = newCorrect;
    team.score = newScore;
    
    // 获取DOM元素
    const scoreElement = document.getElementById(`score-${teamIndex}`);
    const answeredElement = document.getElementById(`answered-${teamIndex}`);
    const accuracyElement = document.getElementById(`accuracy-${teamIndex}`);
    const cardElement = scoreElement.closest('.score-card');
    
    // 移除之前的动画类
    scoreElement.classList.remove('score-pulse', 'score-pulse-minus');
    cardElement.classList.remove('full-screen-highlight', 'minus-screen-highlight');
    
    // 强制重排以触发动画
    void scoreElement.offsetWidth;
    void cardElement.offsetWidth;
    
    if (isPositive) {
        // 应用加分动画效果
        scoreElement.classList.add('score-pulse');
        cardElement.classList.add('full-screen-highlight');
        
        // 创建加分数字弹出效果
        const plusElement = document.createElement('div');
        plusElement.className = 'plus-number';
        plusElement.textContent = `+${scoreChange}`;
        plusElement.style.left = '50%';
        plusElement.style.top = '50%';
        cardElement.appendChild(plusElement);
        
        // 动画结束后移除加分数字
        setTimeout(() => {
            if (cardElement.contains(plusElement)) {
                cardElement.removeChild(plusElement);
            }
        }, 1500);
    } else {
        // 应用减分动画效果
        scoreElement.classList.add('score-pulse-minus');
        cardElement.classList.add('minus-screen-highlight');
        
        // 创建减分数字弹出效果
        const minusElement = document.createElement('div');
        minusElement.className = 'minus-number';
        minusElement.textContent = `${scoreChange}`;
        minusElement.style.left = '50%';
        minusElement.style.top = '50%';
        cardElement.appendChild(minusElement);
        
        // 动画结束后移除减分数字
        setTimeout(() => {
            if (cardElement.contains(minusElement)) {
                cardElement.removeChild(minusElement);
            }
        }, 1500);
    }
    
    // 更新分数显示（在动画开始后）
    setTimeout(() => {
        scoreElement.textContent = newScore;
    }, 300);
    
    // 更新其他数据
    answeredElement.textContent = `已答: ${newAnswered}`;
    
    const accuracy = calculateAccuracy(newCorrect, newAnswered);
    accuracyElement.textContent = `正确率: ${accuracy}%`;
    
    // 根据正确率改变颜色
    if (accuracy >= 80) {
        accuracyElement.style.color = '#4CAF50';
    } else if (accuracy >= 60) {
        accuracyElement.style.color = '#FF9800';
    } else {
        accuracyElement.style.color = '#F44336';
    }
    
    // 动画结束后移除动画类
    setTimeout(() => {
        scoreElement.classList.remove('score-pulse', 'score-pulse-minus');
        cardElement.classList.remove('full-screen-highlight', 'minus-screen-highlight');
    }, 1500);
}

// 随机更新1到5个队伍的数据
function randomUpdate() {
    const updateCount = Math.floor(Math.random() * 5) + 1; // 1到5个队伍
    const updatedIndices = new Set();
    
    // 确保不重复更新同一个队伍
    while (updatedIndices.size < updateCount) {
        const randomIndex = Math.floor(Math.random() * teams.length);
        updatedIndices.add(randomIndex);
    }
    
    // 对每个选中的队伍进行更新
    updatedIndices.forEach(index => {
        updateTeamData(index);
    });
}

// 初始化所有队伍数据
function initializeTeamData() {
    teams.forEach((team, index) => {
        team.answered = Math.floor(Math.random() * 20) + 5;
        team.correct = Math.floor(Math.random() * team.answered);
        team.score = Math.floor(Math.random() * 100) + 50;
        
        // 更新DOM
        document.getElementById(`score-${index}`).textContent = team.score;
        document.getElementById(`answered-${index}`).textContent = `已答: ${team.answered}`;
        
        const accuracy = calculateAccuracy(team.correct, team.answered);
        document.getElementById(`accuracy-${index}`).textContent = `正确率: ${accuracy}%`;
        
        // 根据正确率改变颜色
        const accuracyElement = document.getElementById(`accuracy-${index}`);
        if (accuracy >= 80) {
            accuracyElement.style.color = '#4CAF50';
        } else if (accuracy >= 60) {
            accuracyElement.style.color = '#FF9800';
        } else {
            accuracyElement.style.color = '#F44336';
        }
    });
}

// 倒计时功能
let countdownTime = 120 * 60; // 120分钟倒计时（7200秒）

function updateCountdown() {
    const minutes = Math.floor(countdownTime / 60);
    const seconds = countdownTime % 60;
    
    const countdownElement = document.getElementById('countdown');
    countdownElement.textContent = 
        `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
    
    // 当倒计时少于10分钟时改变颜色为红色
    if (countdownTime <= 600) {
        countdownElement.style.color = '#FF4444';
    }
    
    if (countdownTime > 0) {
        countdownTime--;
    } else {
        // 倒计时结束
        countdownElement.textContent = '时间到！';
        countdownElement.style.color = '#FF0000';
    }
}

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    initializeGrid();
    initializeTeamData();
    
    // 初始化倒计时
    updateCountdown();
    setInterval(updateCountdown, 1000);
    
    // 每2秒随机更新1到5个队伍的数据
    setInterval(randomUpdate, 2000);
});

// 添加手动更新功能（用于演示）
function manualUpdate(teamIndex) {
    if (teamIndex >= 0 && teamIndex < teams.length) {
        updateTeamData(teamIndex);
    }
}

// 手动加分功能
function addScore(teamIndex) {
    if (teamIndex >= 0 && teamIndex < teams.length) {
        const team = teams[teamIndex];
        team.score += 10;
        document.getElementById(`score-${teamIndex}`).textContent = team.score;
        updateTeamData(teamIndex);
    }
}

// 手动减分功能
function minusScore(teamIndex) {
    if (teamIndex >= 0 && teamIndex < teams.length) {
        const team = teams[teamIndex];
        team.score = Math.max(0, team.score - 10);
        document.getElementById(`score-${teamIndex}`).textContent = team.score;
        updateTeamData(teamIndex);
    }
}

// 导出函数供控制台调试使用
window.manualUpdate = manualUpdate;
window.teams = teams;
window.addTeam = addTeam;
window.removeTeam = removeTeam;
window.addScore = addScore;
window.minusScore = minusScore;
