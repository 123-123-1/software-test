<template>
  <div style="height:75vh">
    <div style="display: flex; flex-direction: row; width:80vw">
      <div style="margin-left:0">
        <el-button type="success" @click="startTest">
          开始测试<el-icon class="el-icon--right"><Upload /></el-icon>
        </el-button>
        <el-button type="danger" @click="reset">重置</el-button>
      </div>
      <div style="margin-left:20px">
        <el-text>测试用例数：{{ caseNum == 0 ? null : caseNum }}</el-text>
        <el-text style="margin-left:20px">测试通过数：{{ casePassed == 0 ? null : casePassed }}</el-text>
      </div>
    </div>
    <el-table border :data="data" style="width: 80vw; max-height:70vh; overflow-x: auto;overflow-y: auto;">
      <el-table-column prop="no" label="用例编号" />
      <el-table-column prop="host" label="主机" />
      <el-table-column prop="monitor" label="显示器" />
      <el-table-column prop="peripheral" label="外设" />
      <el-table-column prop="expected" label="期望值" />
      <el-table-column prop="actual" label="实际值" />
      <el-table-column prop="result" label="测试结果" />
    </el-table>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { calcSale } from '@/assets/funcs/computer_sale.js'
import { Upload } from '@element-plus/icons-vue'

const caseNum = ref(0)
const casePassed = ref(0)

// 边界值分析+等价类+异常
const data = reactive([
  // 有效等价类/边界
  { no: 1, host: 1, monitor: 1, peripheral: 1, expected: '销售额：100 元，佣金率：10%，佣金：10.00 元', actual: '', result: '' },
  { no: 2, host: 70, monitor: 80, peripheral: 90, expected: '销售额：6550 元，佣金率：20%，佣金：1310.00 元', actual: '', result: '' },
  { no: 3, host: 40, monitor: 40, peripheral: 40, expected: '销售额：4000 元，佣金率：20%，佣金：800.00 元', actual: '', result: '' },
  // 佣金边界
  { no: 4, host: 13, monitor: 13, peripheral: 13, expected: '销售额：1300 元，佣金率：15%，佣金：195.00 元', actual: '', result: '' },
  { no: 5, host: 40, monitor: 0, peripheral: 40, expected: '显示器数量必须在1~80之间', actual: '', result: '' },
  { no: 6, host: 0, monitor: 40, peripheral: 40, expected: '主机数量必须在1~70之间', actual: '', result: '' },
  { no: 7, host: 40, monitor: 40, peripheral: 0, expected: '外设数量必须在1~90之间', actual: '', result: '' },
  // 错误等价类/越界
  { no: 8, host: 0, monitor: 1, peripheral: 1, expected: '主机数量必须在1~70之间', actual: '', result: '' },
  { no: 9, host: 1, monitor: 0, peripheral: 1, expected: '显示器数量必须在1~80之间', actual: '', result: '' },
  { no: 10, host: 1, monitor: 1, peripheral: 0, expected: '外设数量必须在1~90之间', actual: '', result: '' },
  { no: 11, host: 71, monitor: 1, peripheral: 1, expected: '主机数量必须在1~70之间', actual: '', result: '' },
  { no: 12, host: 1, monitor: 81, peripheral: 1, expected: '显示器数量必须在1~80之间', actual: '', result: '' },
  { no: 13, host: 1, monitor: 1, peripheral: 91, expected: '外设数量必须在1~90之间', actual: '', result: '' },
  // 佣金率边界
  { no: 14, host: 10, monitor: 10, peripheral: 10, expected: '销售额：1000 元，佣金率：10%，佣金：100.00 元', actual: '', result: '' },
  { no: 15, host: 11, monitor: 11, peripheral: 11, expected: '销售额：1100 元，佣金率：15%，佣金：165.00 元', actual: '', result: '' },
  { no: 16, host: 24, monitor: 24, peripheral: 24, expected: '销售额：2400 元，佣金率：20%，佣金：480.00 元', actual: '', result: '' },
  { no: 17, host: 24, monitor: 23, peripheral: 23, expected: '销售额：2305 元，佣金率：20%，佣金：461.00 元', actual: '', result: '' },
  { no: 18, host: 20, monitor: 20, peripheral: 20, expected: '销售额：2000 元，佣金率：20%，佣金：400.00 元', actual: '', result: '' },
  // 统计特殊
  { no: 19, host: -1, monitor: 0, peripheral: 0, expected: '请输入本月销售数据后再统计', actual: '', result: '' },
])

function startTest() {
  reset()
  for (let i in data) {
    caseNum.value++
    const res = calcSale(Number(data[i].host), Number(data[i].monitor), Number(data[i].peripheral))
    data[i].actual = res.msg
    data[i].result = (data[i].actual === data[i].expected) ? "通过" : "未通过"
    if (data[i].result === "通过") {
      casePassed.value++
    }
  }
}

function reset() {
  caseNum.value = 0
  casePassed.value = 0
  for (let i in data) {
    data[i].actual = ""
    data[i].result = ""
  }
}
</script>

<style scoped>
.tester-container {
  max-width: 600px;
  margin: 40px auto;
  background: #fff;
  padding: 24px;
  border-radius: 8px;
}
.input-form {
  margin-bottom: 16px;
}
.result {
  margin: 16px 0;
}
</style>