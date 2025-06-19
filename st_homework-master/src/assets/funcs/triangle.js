/**
 * 判断三角形类型
 * @param {number} a 
 * @param {number} b 
 * @param {number} c 
 * @return {string}
 */
function triangle(a, b, c) {
    
    if (a <= 0 || b <= 0 || c <= 0 || a > 256 || b > 256 || c > 256) {
        return "输入越界";
    }

    if (a + b > c && a + c > b && b + c > a) {
        if (a === b && b === c) {
            return "等边三角形";
        } else if (a === b || b === c || a === c) {
            return "等腰三角形";
        } else {
            return "普通三角形";
        }
    } else {
        return "不构成三角形";
    }
}

export default triangle