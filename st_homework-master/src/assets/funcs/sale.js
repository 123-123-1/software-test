/**
 * 第16题：销售系统佣金计算
 * @param {number} sale 年销售额（元）
 * @param {number} day 请假天数（天）
 * @param {number} rate 现金到账率（百分比，0-100）
 * @returns {[number|string, string]} [佣金, 信息]
 */
function saleSys(sale, day, rate) {
    // 参数校验
    if (typeof sale !== 'number' || typeof day !== 'number' || typeof rate !== 'number' ||
        sale < 0 || day < 0 || day > 366 || rate < 0 || rate > 100 || !Number.isFinite(sale) || !Number.isFinite(day) || !Number.isFinite(rate)) {
        return [0, "参数非法"];
    }
    // 题目主逻辑
    if (sale > 2000000 && day <= 10) {
        if (rate >= 60) {
            // 佣金系数7
            return [(sale / 7).toFixed(2), "正确"];
        } else {
            // 现金到账<60%，佣金不予计算
            return [0, "正确"];
        }
    } else {
        if (rate > 85) {
            // 佣金系数5
            return [(sale / 5).toFixed(2), "正确"];
        } else {
            // 佣金系数6
            return [(sale / 6).toFixed(2), "正确"];
        }
    }
}

export default saleSys;