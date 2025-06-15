<template>
    <div class="unit-test-wrapper">
        <div class="unit-test-header">
            <el-button type="success" @click="runAllTests" :loading="loading">
                {{ loading ? '测试中...' : '运行所有测试' }}
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
                class="unit-test-table"
                height="100%"
                highlight-current-row
            >
                <el-table-column prop="name" label="测试用例" width="200" />
                <el-table-column label="测试方法" width="300">
                    <template #default="scope">
                        <div>
                            <el-descriptions :column="1" size="small" border style="margin-bottom: 4px;">
                                <el-descriptions-item v-for="(val, key) in scope.row.method" :key="key" :label="key">
                                    {{ val }}
                                </el-descriptions-item>
                            </el-descriptions>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="expect" label="预期结果" width="200" />
                <el-table-column prop="actual" label="实际结果" width="200">
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
            title="测试用例详情"
            width="600px"
            @close="dialogVisible = false"
            @update:modelValue="val => dialogVisible = val"
        >
            <div v-if="currentDetail">
                <b>测试用例：</b>{{ currentDetail.name }}<br/>
                <b>测试方法：</b>
                <el-descriptions :column="1" size="small" border style="margin-bottom: 8px;">
                    <el-descriptions-item v-for="(val, key) in currentDetail.method" :key="key" :label="key">
                        {{ val }}
                    </el-descriptions-item>
                </el-descriptions>
                <b>预期结果：</b>{{ currentDetail.expect }}<br/>
                <b>实际结果：</b>
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
        name: 'sendMessage_Success',
        method: {
            '测试方法': 'sendMessage',
            '参数': 'userId=1, chatId=1, content="Test message"',
            '测试目标': '验证成功发送消息'
        },
        expect: '消息发送成功，返回正确的Message对象',
        actual: '-',
        pass: undefined,
        duration: '-',
        loading: false
    },
    {
        name: 'sendMessage_UserNotInChat',
        method: {
            '测试方法': 'sendMessage',
            '参数': 'userId=1, chatId=1, content="Test message"',
            '测试目标': '验证非群聊成员发送消息失败'
        },
        expect: '抛出异常，提示"该用户并非该群聊的成员"',
        actual: '-',
        pass: undefined,
        duration: '-',
        loading: false
    },
    {
        name: 'getChatHistory_Success',
        method: {
            '测试方法': 'getChatHistory',
            '参数': 'chatId=1, userId=1',
            '测试目标': '验证成功获取聊天历史'
        },
        expect: '返回包含两条消息的列表，每条消息包含正确的用户信息',
        actual: '-',
        pass: undefined,
        duration: '-',
        loading: false
    },
    {
        name: 'getChatHistory_UserNotInChat',
        method: {
            '测试方法': 'getChatHistory',
            '参数': 'chatId=1, userId=1',
            '测试目标': '验证非群聊成员获取聊天历史失败'
        },
        expect: '抛出异常，提示"该用户并非该群聊的成员"',
        actual: '-',
        pass: undefined,
        duration: '-',
        loading: false
    },
    {
        name: 'deleteMsg_Success',
        method: {
            '测试方法': 'deleteMsg',
            '参数': 'userId=1, messageId=1',
            '测试目标': '验证成功删除消息'
        },
        expect: '返回成功消息，状态为1',
        actual: '-',
        pass: undefined,
        duration: '-',
        loading: false
    },
    {
        name: 'deleteMsg_Failure',
        method: {
            '测试方法': 'deleteMsg',
            '参数': 'userId=1, messageId=1',
            '测试目标': '验证删除消息失败'
        },
        expect: '抛出异常，提示"撤回该信息失败"',
        actual: '-',
        pass: undefined,
        duration: '-',
        loading: false
    }
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
    if (typeof actual === 'object') return JSON.stringify(actual, null, 2)
    if (typeof actual === 'string' && (actual.startsWith('{') || actual.startsWith('['))) {
        try {
            return JSON.stringify(JSON.parse(actual), null, 2)
        } catch { return actual }
    }
    return actual
}

// 运行所有测试
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
        const res = await fetch('http://localhost:8080/api/testtools/unit/run', {
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
            ElMessage.error('单元测试接口返回失败')
            status.value = 'init'
        }
    } catch (e) {
        ElMessage.error('单元测试接口调用失败: ' + e)
        status.value = 'init'
    }
    loading.value = false
}

// 运行单个测试
async function runSingleTest(index) {
    const tc = testCases.value[index]
    tc.loading = true
    // 清空该用例的结果
    tc.actual = '-'
    tc.pass = undefined
    tc.duration = '-'
    try {
        const res = await fetch('http://localhost:8080/api/testtools/unit/run', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify([{...tc, loading: undefined}])
        })
        const data = await res.json()
        if (data.success && data.results.length > 0) {
            Object.assign(tc, data.results[0])
        } else {
            ElMessage.error('单元测试接口返回失败')
        }
    } catch (e) {
        ElMessage.error('单元测试接口调用失败: ' + e)
    }
    tc.loading = false
}

function showDetail(row) {
    currentDetail.value = row
    dialogVisible.value = true
}
</script>

<style>
.unit-test-wrapper {
    min-height: 100vh;
    background: #f5f6fa;
    padding: 0;
    margin: 0;
    display: flex;
    flex-direction: column;
}
.unit-test-header {
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
.unit-test-table {
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