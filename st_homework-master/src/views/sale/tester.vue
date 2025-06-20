<template>
    <div style="height:75vh">
        <div style="display: flex; flex-direction: row; width:80vw">
            <el-select v-model="value" style="max-width:200px" @change="updateData">
                <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
            <div style="margin-left:20px">
                <el-button type="success" @click="startTest">
                    开始测试<el-icon class="el-icon--right">
                        <Upload />
                    </el-icon>
                </el-button>
                <el-button type="danger" @click="reset">
                    重置
                </el-button>
            </div>
            <div style="margin-left:20px">
                <el-text>
                    测试用例数：{{ caseNum == 0 ? null : caseNum }}
                </el-text>
                <el-text style="margin-left:20px">
                    测试通过数：{{ casePassed == 0 ? null : casePassed }}
                </el-text>
            </div>
        </div>
        <el-table border :data="data" style="width: 80vw; max-height:70vh; overflow-x: auto;overflow-y: auto;">
            <el-table-column prop="no" label="用例编号" width=auto />
            <el-table-column prop="s" label="总销售额" width=auto />
            <el-table-column prop="d" label="请假天数" width="auto" />
            <el-table-column prop="r" label="资金到账率" width="auto" />
            <el-table-column prop="ecom" label="期望佣金" width="auto" />
            <el-table-column prop="emsg" label="期望信息" width="auto" />
            <el-table-column prop="acom" label="实际佣金" width="auto" />
            <el-table-column prop="amsg" label="实际信息" width="auto" />
            <el-table-column prop="result" label="测试结果" width="auto" />
        </el-table>
    </div>
</template>
<script setup>
import { ref, reactive } from 'vue'
import sale from '@/assets/funcs/sale.js';
import { Upload } from '@element-plus/icons-vue'
const caseName = ["语句覆盖", "判断覆盖", "条件覆盖", "判断-条件覆盖", "条件组合覆盖"]
const value = ref("0")
const options = ref([
    { value: "0", label: "语句覆盖" },
    { value: "1", label: "判断覆盖" },
    { value: "2", label: "条件覆盖" },
    { value: "3", label: "判断-条件覆盖" },
    { value: "4", label: "条件组合覆盖" },
])
const caseNum = ref(0)
const casePassed = ref(0)

const testCases = {
    "语句覆盖": [
        { no: 1, s: 2100000, d: 5, r: 70, ecom: (2100000/7).toFixed(2), emsg: "正确" }, // 主路径
        { no: 2, s: 2100000, d: 5, r: 50, ecom: 0, emsg: "正确" }, // 现金到账<60%
        { no: 3, s: 1000000, d: 20, r: 80, ecom: (1000000/6).toFixed(2), emsg: "正确" }, // 其他情况，到账<=85%
        { no: 4, s: 1000000, d: 20, r: 90, ecom: (1000000/5).toFixed(2), emsg: "正确" }, // 其他情况，到账>85%
    ],
    "判断覆盖": [
        { no: 1, s: 2100000, d: 5, r: 70, ecom: (2100000/7).toFixed(2), emsg: "正确" }, // if1 true, if2 false
        { no: 2, s: 2100000, d: 5, r: 50, ecom: 0, emsg: "正确" }, // if1 true, if2 true
        { no: 3, s: 1000000, d: 20, r: 80, ecom: (1000000/6).toFixed(2), emsg: "正确" }, // if1 false, if3 true
        { no: 4, s: 1000000, d: 20, r: 90, ecom: (1000000/5).toFixed(2), emsg: "正确" }, // if1 false, if3 false
    ],
    "条件覆盖": [
        { no: 1, s: 2100000, d: 5, r: 70, ecom: (2100000/7).toFixed(2), emsg: "正确" }, // sale>2e6 true
        { no: 2, s: 1900000, d: 5, r: 70, ecom: (1900000/6).toFixed(2), emsg: "正确" }, // sale>2e6 false
        { no: 3, s: 2100000, d: 15, r: 70, ecom: (2100000/6).toFixed(2), emsg: "正确" }, // day<=10 false
        { no: 4, s: 2100000, d: 5, r: 50, ecom: 0, emsg: "正确" }, // rate<60 true
        { no: 5, s: 2100000, d: 5, r: 70, ecom: (2100000/7).toFixed(2), emsg: "正确" }, // rate<60 false
        { no: 6, s: 1000000, d: 20, r: 80, ecom: (1000000/6).toFixed(2), emsg: "正确" }, // rate<=85 true
        { no: 7, s: 1000000, d: 20, r: 90, ecom: (1000000/5).toFixed(2), emsg: "正确" }, // rate<=85 false
    ],
    "判断-条件覆盖": [
        { no: 1, s: 2100000, d: 5, r: 50, ecom: 0, emsg: "正确" }, // if1 true, if2 true
        { no: 2, s: 2100000, d: 5, r: 70, ecom: (2100000/7).toFixed(2), emsg: "正确" }, // if1 true, if2 false
        { no: 3, s: 2100000, d: 15, r: 70, ecom: (2100000/6).toFixed(2), emsg: "正确" }, // if1 false, if3 true
        { no: 4, s: 2100000, d: 15, r: 90, ecom: (2100000/5).toFixed(2), emsg: "正确" }, // if1 false, if3 false
    ],
    "条件组合覆盖": [
        { no: 1, s: 2100000, d: 5, r: 70, ecom: (2100000/7).toFixed(2), emsg: "正确" }, // T T T
        { no: 2, s: 2100000, d: 5, r: 50, ecom: 0, emsg: "正确" }, // T T F
        { no: 3, s: 2100000, d: 15, r: 70, ecom: (2100000/6).toFixed(2), emsg: "正确" }, // T F T
        { no: 4, s: 2100000, d: 15, r: 50, ecom: (2100000/6).toFixed(2), emsg: "正确" }, // T F F
        { no: 5, s: 1900000, d: 5, r: 90, ecom: (1900000/5).toFixed(2), emsg: "正确" }, // F T T
        { no: 6, s: 1900000, d: 5, r: 80, ecom: (1900000/6).toFixed(2), emsg: "正确" }, // F T F
        { no: 7, s: 1900000, d: 15, r: 90, ecom: (1900000/5).toFixed(2), emsg: "正确" }, // F F T
        { no: 8, s: 1900000, d: 15, r: 80, ecom: (1900000/6).toFixed(2), emsg: "正确" }, // F F F
    ],
}

let data = reactive(testCases[caseName[value.value]])
function updateData() {
    reset()
    data = reactive(testCases[caseName[value.value]])
}

function startTest() {
    reset()
    for (let i in data) {
        caseNum.value++
        let ret = sale(data[i].s, data[i].d, data[i].r)
        data[i].acom = ret[0]
        data[i].amsg = ret[1]
        data[i].result = (data[i].acom == data[i].ecom && data[i].amsg == data[i].emsg) ? "通过" : "不通过"
        if (data[i].result == "通过") {
            casePassed.value++;
        }
    }
}

function reset() {
    caseNum.value = 0
    casePassed.value = 0
    for (let i in data) {
        data[i].acom = ""
        data[i].amsg = ""
        data[i].result = ""
    }
}

</script>
<style>
.el-table .warning-row {
    --el-table-tr-bg-color: var(--el-color-warning-light-9);
}

.el-table .success-row {
    --el-table-tr-bg-color: var(--el-color-success-light-9);
}
</style>