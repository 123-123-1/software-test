<template>
    <div class="system-test-wrapper">
        <div class="system-test-header">
            <el-button type="success" @click="runAllTests" :loading="loading">
                {{ loading ? '测试中...' : '测试全部' }}
                <el-icon class="el-icon--right"><Upload /></el-icon>
            </el-button>
            <el-text style="margin-left:30px;">
                <b>测试状态：</b>
                <span :style="{color: statusColor}">
                    {{ statusText }}
                </span>
            </el-text>
            <el-text style="margin-left:30px;">
                测试用例数：{{ testCases.length || '-' }}
            </el-text>
            <el-text style="margin-left:20px;">
                测试通过数：{{ passCount || '-' }}
            </el-text>
        </div>
        <div class="table-bg">
            <el-table
                border
                :data="testCases"
                class="system-test-table"
                height="100%"
                highlight-current-row
            >
                <el-table-column prop="name" label="用例名称" width="160" />
                <el-table-column label="用例设计" width="340">
                    <template #default="scope">
                        <div>
                            <el-descriptions :column="1" size="small" border style="margin-bottom: 4px;">
                                <el-descriptions-item v-for="(val, key) in scope.row.design" :key="key" :label="key">
                                    {{ val }}
                                </el-descriptions-item>
                            </el-descriptions>
                            <span>{{ scope.row.method }}</span>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="expect" label="预期" width="160" />
                <el-table-column prop="actual" label="实际" width="200">
                    <template #default="scope">
                        <pre style="margin:0; background: #f8f8f8; white-space:pre-wrap;">{{ formatActual(scope.row.actual) }}</pre>
                    </template>
                </el-table-column>
                <el-table-column prop="pass" label="结果" width="90">
                    <template #default="scope">
                        <span v-if="scope.row.pass === undefined" style="color:gray;">未测试</span>
                        <span v-else-if="scope.row.pass" style="color:green;font-weight:bold;">通过</span>
                        <span v-else style="color:red;font-weight:bold;">失败</span>
                    </template>
                </el-table-column>
                <el-table-column prop="duration" label="测试时长" width="100">
                    <template #default="scope">
                        <span v-if="scope.row.duration && scope.row.duration !== '-'">{{ scope.row.duration }}</span>
                        <span v-else>-</span>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="100">
                    <template #default="scope">
                        <el-button size="small" @click="runSingleTest(scope.$index)" :loading="scope.row.loading">测试</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <el-dialog
            :model-value="dialogVisible"
            title="用例详情"
            width="600px"
            @close="dialogVisible = false"
            @update:modelValue="val => dialogVisible = val"
        >
            <div v-if="currentDetail">
                <b>用例名称：</b>{{ currentDetail.name }}<br/>
                <b>参数：</b>
                <el-descriptions :column="1" size="small" border style="margin-bottom: 8px;">
                    <el-descriptions-item v-for="(val, key) in currentDetail.design" :key="key" :label="key">
                        {{ val }}
                    </el-descriptions-item>
                </el-descriptions>
                <b>方法：</b>{{ currentDetail.method }}<br/>
                <b>预期：</b>{{ currentDetail.expect }}<br/>
                <b>实际：</b>
                <pre>{{ formatActual(currentDetail.actual) }}</pre>
                <b>结果：</b>
                <span v-if="currentDetail.pass === undefined" style="color:gray;">未测试</span>
                <span v-else-if="currentDetail.pass" style="color:green;font-weight:bold;">通过</span>
                <span v-else style="color:red;font-weight:bold;">失败</span>
                <br/>
                <b>测试时长：</b>{{ currentDetail.duration || '-' }}
            </div>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'

const testCases = ref([
    {
        name: '用户注册',
        design: { userName: 'testuser1234', password: '1234567' },
        method: 'POST /api/users/registration，输入用户名和密码，期望注册成功',
        expect: '注册成功，可使用新账号登录',
        actual: '-',
        pass: undefined,
        duration: '-',
        loading: false
    },
    {
        name: '个人场地预约',
        design: {
            userName: '1234', password: '1234', venueId: 1, date: '2024-10-01', time: '19:00-21:00', people: 5
        },
        method: '登录后预约场地，选择时间段和人数，提交并支付，期望订单状态为已支付',
        expect: '个人预约订单创建成功，状态为已支付',
        actual: '-', pass: undefined, duration: '-', loading: false
    },
    {
        name: '发起拼场预约',
        design: {
            userName: '13900139000', password: '456', venueId: 2, date: '2024-10-02', time: '14:00-16:00', totalPeople: 8, recruit: 7
        },
        method: '登录后预约场地，选择拼场，填写人数和时间，提交并支付，期望订单状态为招募中',
        expect: '拼场预约发起成功，状态为招募中',
        actual: '-', pass: undefined, duration: '-', loading: false
    },
    {
        name: '团体预约场地',
        design: {
            userName: '15000150000', password: '789', groupId: 1, venueId: 3, date: '2024-10-03', time: '20:00-22:00', people: 12
        },
        method: '团队创建者登录，选择团体预约，填写场地和时间，提交并支付，期望订单状态为已支付',
        expect: '团体预约订单创建成功，状态为已支付',
        actual: '-', pass: undefined, duration: '-', loading: false
    },
    {
        name: '创建运动活动',
        design: {
            userName: '13900139000', password: '456', title: '周末羽毛球局', type: '羽毛球', date: '2024-10-05', time: '15:00-17:00', place: '羽毛球场D', maxPeople: 6, desc: '活动描述'
        },
        method: '登录后发起活动，填写信息并发布，期望活动状态为招募中',
        expect: '运动活动创建成功，状态为招募中',
        actual: '-', pass: undefined, duration: '-', loading: false
    },
    {
        name: '用户登录（密码错误）',
        design: { userName: '1234', password: '12345' },
        method: '输入错误密码登录，期望提示密码错误',
        expect: '登录失败，停留在登录页面',
        actual: '-', pass: undefined, duration: '-', loading: false
    },
    {
        name: '取消个人预约',
        design: { userName: '1234', password: '1234', reservationId: 1003 },
        method: '登录后进入我的预约，选择订单点击取消，确认后期望状态变已取消',
        expect: '预约取消成功，释放场地时段',
        actual: '-', pass: undefined, duration: '-', loading: false
    },
    {
        name: '拒绝好友申请',
        design: { userName: '13900139000', password: '456', applicationId: 2001 },
        method: '登录后进入好友申请，点击拒绝，期望申请状态变已拒绝',
        expect: '申请被拒绝，双方无好友关系',
        actual: '-', pass: undefined, duration: '-', loading: false
    },
    {
        name: '解散运动团体',
        design: { userName: '15000150000', password: '789', groupId: 1 },
        method: '团体创建者登录，进入管理，点击解散团体，确认后期望状态变已解散',
        expect: '团体解散成功，成员关系解除',
        actual: '-', pass: undefined, duration: '-', loading: false
    }
    // 可继续添加更多用例
])

const passCount = computed(() => testCases.value.filter(r => r.pass).length)
const loading = ref(false)
const status = ref('init') // init, running, done
const dialogVisible = ref(false)
const currentDetail = ref(null)

const statusText = computed(() => {
    if (status.value === 'init') return '未测试'
    if (status.value === 'running') return '测试中...'
    if (status.value === 'done') return '已完成'
    return ''
})
const statusColor = computed(() => {
    if (status.value === 'init') return 'gray'
    if (status.value === 'running') return '#409EFF'
    if (status.value === 'done') return 'green'
    return 'gray'
})

function formatActual(actual) {
    if (typeof actual === 'string' && actual.includes('No database selected')) {
        return '后端数据库未配置或未创建，请联系管理员。';
    }
    if (typeof actual === 'string' && actual.includes('JDBC exception')) {
        return '后端数据库异常，请联系管理员。';
    }
    if (typeof actual === 'string' && actual.includes('ossClient') && actual.includes('null')) {
        return '后端OSS对象存储未配置或未初始化，请联系管理员。';
    }
    if (typeof actual === 'object') return JSON.stringify(actual, null, 2)
    if (typeof actual === 'string' && (actual.startsWith('{') || actual.startsWith('['))) {
        try {
            return JSON.stringify(JSON.parse(actual), null, 2)
        } catch { return actual }
    }
    return actual
}

// 测试全部
async function runAllTests() {
    loading.value = true
    status.value = 'running'
    // 清空所有用例的结果
    testCases.value.forEach(tc => {
        tc.actual = '-'
        tc.pass = undefined
        tc.duration = '-'
        tc.loading = false
    })
    try {
        const res = await fetch('http://localhost:8080/api/testtools/system/run', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(testCases.value.map(({loading, ...rest}) => rest))
        })
        const data = await res.json()
        if (data.success) {
            // 更新每个用例的结果
            data.results.forEach((r, i) => {
                Object.assign(testCases.value[i], r)
            })
            status.value = 'done'
        } else {
            ElMessage.error('系统测试接口返回失败')
            status.value = 'init'
        }
    } catch (e) {
        ElMessage.error('系统测试接口调用失败: ' + e)
        status.value = 'init'
    }
    loading.value = false
}

// 单个用例测试
async function runSingleTest(index) {
    const tc = testCases.value[index]
    tc.loading = true
    // 清空该用例的结果
    tc.actual = '-'
    tc.pass = undefined
    tc.duration = '-'
    try {
        const res = await fetch('http://localhost:8080/api/testtools/system/run', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify([{...tc, loading: undefined}])
        })
        const data = await res.json()
        if (data.success && data.results.length > 0) {
            Object.assign(tc, data.results[0])
        } else {
            ElMessage.error('系统测试接口返回失败')
        }
    } catch (e) {
        ElMessage.error('系统测试接口调用失败: ' + e)
    }
    tc.loading = false
}

function showDetail(row) {
    currentDetail.value = row
    dialogVisible.value = true
}
</script>

<style>
.system-test-wrapper {
    min-height: 100vh;
    background: #f5f6fa;
    padding: 0;
    margin: 0;
    display: flex;
    flex-direction: column;
}
.system-test-header {
    flex-shrink: 0;
    padding-top: 20px;
    padding-bottom: 10px;
    background: #f5f6fa;
    width: 100vw;
}
.table-bg {
    background: #fff;
    width: 100vw;
    flex: 1 1 0;
    min-height: 0;
    box-sizing: border-box;
    padding: 0 0 30px 0;
    margin-top: 0;
    display: flex;
    flex-direction: column;
    height: 100%;
}
.system-test-table {
    flex: 1 1 0;
    height: 100% !important;
    min-height: 0;
}
.el-table .warning-row {
    --el-table-tr-bg-color: var(--el-color-warning-light-9);
}
.el-table .success-row {
    --el-table-tr-bg-color: var(--el-color-success-light-9);
}
</style>