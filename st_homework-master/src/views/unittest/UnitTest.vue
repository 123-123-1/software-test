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
    // MessageServiceTest 的测试用例
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
    },
    // UserServiceTest 的测试用例
    {
        name: 'login_Success',
        method: {
            '测试方法': 'login',
            '参数': 'username="test", password="password"',
            '测试目标': '验证用户登录成功'
        },
        expect: '返回用户信息和登录token',
        actual: '-',
        pass: undefined,
        duration: '-',
        loading: false
    },
    {
        name: 'login_UserNotFound',
        method: {
            '测试方法': 'login',
            '参数': 'username="nonexistent", password="password"',
            '测试目标': '验证不存在用户登录失败'
        },
        expect: '抛出异常，提示"用户不存在"',
        actual: '-',
        pass: undefined,
        duration: '-',
        loading: false
    },
    {
        name: 'login_WrongPassword',
        method: {
            '测试方法': 'login',
            '参数': 'username="test", password="wrong"',
            '测试目标': '验证密码错误登录失败'
        },
        expect: '抛出异常，提示"密码错误"',
        actual: '-',
        pass: undefined,
        duration: '-',
        loading: false
    },
    {
        name: 'register_Success_RegularUser',
        method: {
            '测试方法': 'register',
            '参数': 'userInfo={username:"new", password:"pwd", role:"USER"}',
            '测试目标': '验证普通用户注册成功'
        },
        expect: '返回注册成功信息',
        actual: '-',
        pass: undefined,
        duration: '-',
        loading: false
    },
    {
        name: 'register_Success_VenueAdmin',
        method: {
            '测试方法': 'register',
            '参数': 'userInfo={username:"admin", password:"pwd", role:"VENUE_ADMIN"}',
            '测试目标': '验证场馆管理员注册成功'
        },
        expect: '返回注册成功信息',
        actual: '-',
        pass: undefined,
        duration: '-',
        loading: false
    },
    {
        name: 'register_UserAlreadyExists',
        method: {
            '测试方法': 'register',
            '参数': 'userInfo={username:"existing", password:"pwd"}',
            '测试目标': '验证重复用户名注册失败'
        },
        expect: '抛出异常，提示"用户名已存在"',
        actual: '-',
        pass: undefined,
        duration: '-',
        loading: false
    },
    {
        name: 'isUserAdmin_True',
        method: {
            '测试方法': 'isUserAdmin',
            '参数': 'userId=1',
            '测试目标': '验证管理员用户身份'
        },
        expect: '返回true',
        actual: '-',
        pass: undefined,
        duration: '-',
        loading: false
    },
    {
        name: 'isUserAdmin_False',
        method: {
            '测试方法': 'isUserAdmin',
            '参数': 'userId=2',
            '测试目标': '验证非管理员用户身份'
        },
        expect: '返回false',
        actual: '-',
        pass: undefined,
        duration: '-',
        loading: false
    },
    {
        name: 'isUserAdmin_UserNotFound',
        method: {
            '测试方法': 'isUserAdmin',
            '参数': 'userId=999',
            '测试目标': '验证不存在用户的管理员判断'
        },
        expect: '抛出异常，提示"用户不存在"',
        actual: '-',
        pass: undefined,
        duration: '-',
        loading: false
    },
    {
        name: 'getUserInfo_Success',
        method: {
            '测试方法': 'getUserInfo',
            '参数': 'userId=1',
            '测试目标': '验证成功获取用户信息'
        },
        expect: '返回正确的用户信息',
        actual: '-',
        pass: undefined,
        duration: '-',
        loading: false
    },
    {
        name: 'getUserInfo_UserNotFound',
        method: {
            '测试方法': 'getUserInfo',
            '参数': 'userId=999',
            '测试目标': '验证获取不存在用户信息失败'
        },
        expect: '抛出异常，提示"用户不存在"',
        actual: '-',
        pass: undefined,
        duration: '-',
        loading: false
    },
    {
        name: 'getUserProfile_Success',
        method: {
            '测试方法': 'getUserProfile',
            '参数': 'userId=1',
            '测试目标': '验证成功获取用户档案'
        },
        expect: '返回用户的完整档案信息',
        actual: '-',
        pass: undefined,
        duration: '-',
        loading: false
    },
    {
        name: 'getUserProfile_UserNotFound',
        method: {
            '测试方法': 'getUserProfile',
            '参数': 'userId=999',
            '测试目标': '验证获取不存在用户档案失败'
        },
        expect: '抛出异常，提示"用户不存在"',
        actual: '-',
        pass: undefined,
        duration: '-',
        loading: false
    },
    {
        name: 'getUserPhoto_Success',
        method: {
            '测试方法': 'getUserPhoto',
            '参数': 'userId=1',
            '测试目标': '验证成功获取用户头像'
        },
        expect: '返回用户头像URL',
        actual: '-',
        pass: undefined,
        duration: '-',
        loading: false
    },
    {
        name: 'updateUserInfo_Success',
        method: {
            '测试方法': 'updateUserInfo',
            '参数': 'userId=1, userInfo={...}',
            '测试目标': '验证成功更新用户信息'
        },
        expect: '返回更新成功信息',
        actual: '-',
        pass: undefined,
        duration: '-',
        loading: false
    },
    {
        name: 'updateUserInfo_UserNotFound',
        method: {
            '测试方法': 'updateUserInfo',
            '参数': 'userId=999, userInfo={...}',
            '测试目标': '验证更新不存在用户信息失败'
        },
        expect: '抛出异常，提示"用户不存在"',
        actual: '-',
        pass: undefined,
        duration: '-',
        loading: false
    },
    {
        name: 'updateUserPwd_Success',
        method: {
            '测试方法': 'updateUserPwd',
            '参数': 'userId=1, oldPwd="old", newPwd="new"',
            '测试目标': '验证成功修改密码'
        },
        expect: '返回密码修改成功信息',
        actual: '-',
        pass: undefined,
        duration: '-',
        loading: false
    },
    {
        name: 'updateUserPwd_UserNotFound',
        method: {
            '测试方法': 'updateUserPwd',
            '参数': 'userId=999, oldPwd="old", newPwd="new"',
            '测试目标': '验证修改不存在用户密码失败'
        },
        expect: '抛出异常，提示"用户不存在"',
        actual: '-',
        pass: undefined,
        duration: '-',
        loading: false
    },
    {
        name: 'updateUserPwd_WrongOldPassword',
        method: {
            '测试方法': 'updateUserPwd',
            '参数': 'userId=1, oldPwd="wrong", newPwd="new"',
            '测试目标': '验证原密码错误修改失败'
        },
        expect: '抛出异常，提示"原密码错误"',
        actual: '-',
        pass: undefined,
        duration: '-',
        loading: false
    },
    {
        name: 'updateUserAvatar_Success',
        method: {
            '测试方法': 'updateUserAvatar',
            '参数': 'userId=1, avatarFile=file',
            '测试目标': '验证成功更新头像'
        },
        expect: '返回头像更新成功信息',
        actual: '-',
        pass: undefined,
        duration: '-',
        loading: false
    },
    {
        name: 'updateUserAvatar_UploadError',
        method: {
            '测试方法': 'updateUserAvatar',
            '参数': 'userId=1, avatarFile=invalidFile',
            '测试目标': '验证头像上传失败'
        },
        expect: '抛出异常，提示"头像上传失败"',
        actual: '-',
        pass: undefined,
        duration: '-',
        loading: false
    },
    {
        name: 'getUsersByName_Success',
        method: {
            '测试方法': 'getUsersByName',
            '参数': 'username="test"',
            '测试目标': '验证成功按用户名搜索'
        },
        expect: '返回匹配的用户列表',
        actual: '-',
        pass: undefined,
        duration: '-',
        loading: false
    },
    {
        name: 'getUsersByName_NoUsersFound',
        method: {
            '测试方法': 'getUsersByName',
            '参数': 'username="nonexistent"',
            '测试目标': '验证搜索无匹配用户'
        },
        expect: '返回空列表',
        actual: '-',
        pass: undefined,
        duration: '-',
        loading: false
    },
    {
        name: 'getUserList_Success',
        method: {
            '测试方法': 'getUserList',
            '参数': 'page=1, size=10',
            '测试目标': '验证成功获取用户列表'
        },
        expect: '返回分页的用户列表',
        actual: '-',
        pass: undefined,
        duration: '-',
        loading: false
    },
    {
        name: 'getUserList_EmptyList',
        method: {
            '测试方法': 'getUserList',
            '参数': 'page=999, size=10',
            '测试目标': '验证获取空页用户列表'
        },
        expect: '返回空列表',
        actual: '-',
        pass: undefined,
        duration: '-',
        loading: false
    },
    {
        name: 'getUserNotification_Success',
        method: {
            '测试方法': 'getUserNotification',
            '参数': 'userId=1',
            '测试目标': '验证成功获取用户通知'
        },
        expect: '返回用户通知列表',
        actual: '-',
        pass: undefined,
        duration: '-',
        loading: false
    },
    {
        name: 'getUserNotification_EmptyList',
        method: {
            '测试方法': 'getUserNotification',
            '参数': 'userId=2',
            '测试目标': '验证获取空通知列表'
        },
        expect: '返回空通知列表',
        actual: '-',
        pass: undefined,
        duration: '-',
        loading: false
    },
    {
        name: 'sendUserNotification_Success',
        method: {
            '测试方法': 'sendUserNotification',
            '参数': 'userId=1, notification={...}',
            '测试目标': '验证成功发送用户通知'
        },
        expect: '返回发送成功信息',
        actual: '-',
        pass: undefined,
        duration: '-',
        loading: false
    },
    {
        name: 'editUserNotification_Success',
        method: {
            '测试方法': 'editUserNotification',
            '参数': 'notificationId=1, content="updated"',
            '测试目标': '验证成功编辑通知'
        },
        expect: '返回编辑成功信息',
        actual: '-',
        pass: undefined,
        duration: '-',
        loading: false
    },
    {
        name: 'editUserNotification_NotFound',
        method: {
            '测试方法': 'editUserNotification',
            '参数': 'notificationId=999, content="updated"',
            '测试目标': '验证编辑不存在通知失败'
        },
        expect: '抛出异常，提示"通知不存在"',
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
        const res = await fetch('http://113.44.64.65:8080/api/testtools/unit/run', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(testCases.value.map(({loading, ...rest}) => rest))
        })
        const data = await res.json()
        if (Array.isArray(data)) {
            // 处理所有测试类的结果
            data.forEach(classResult => {
                classResult.testResults.forEach(result => {
                    // 找到对应的测试用例并更新结果
                    const testCase = testCases.value.find(tc => tc.name === result.methodName)
                    if (testCase) {
                        testCase.actual = `${result.status}\n测试类: ${classResult.className}`
                        testCase.pass = result.status === 'PASSED'
                        testCase.duration = `${result.executionTime}ms`
                    }
                })
            })
            status.value = 'done'
        } else {
            ElMessage.error('单元测试接口返回格式不正确')
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
        const res = await fetch('http://113.44.64.65:8080/api/testtools/unit/run', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify([{...tc, loading: undefined}])
        })
        const data = await res.json()
        if (Array.isArray(data) && data.length > 0) {
            // 处理测试类的结果
            const classResult = data[0]
            const result = classResult.testResults.find(r => r.methodName === tc.name)
            if (result) {
                tc.actual = `${result.status}\n测试类: ${classResult.className}`
                tc.pass = result.status === 'PASSED'
                tc.duration = `${result.executionTime}ms`
            }
        } else {
            ElMessage.error('单元测试接口返回格式不正确')
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