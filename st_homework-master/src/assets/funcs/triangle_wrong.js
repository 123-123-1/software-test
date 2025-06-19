/**
 * 判断三角形类型（错误代码）
 * @param {number} a 第一条边
 * @param {number} b 第二条边
 * @param {number} c 第三条边
 * @return {string}
 */
function triangle_wrong(a, b, c) {
    if (a <= 0 || b <= 0 || c <= 0 || a >= 256 || b >= 256 || c >= 256) {
        return "输入越界";
    }
    if (a + b > c && a + c > b && b + c > a) {
        // 先判断等腰再判断等边，导致等边三角形被误判为等腰
        if (a === b || b === c || a === c) {
            return "等腰三角形";
        } else if (a === b && b === c) {
            return "等边三角形";
        } else {
            return "普通三角形";
        }
    } else {
        return "不构成三角形";
    }
}

export default triangle_wrong; 