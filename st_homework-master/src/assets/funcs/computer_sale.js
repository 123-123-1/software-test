/**
 * 电脑销售系统销售员提成计算接口, 根据输入的月售量返回销售总额, 提成, 运行状态
 * @param {number} frame 主机, 价格25, 月售上限70, 下限为1
 * @param {number} screen 屏幕, 价格30, 月售上限80, 下限为1
 * @param {number} equip 外设, 价格45, 月售上限90, 下限为1
 * @return {object}
 */
const UNIT_PRICE = { host: 25, monitor: 30, peripheral: 45 }
const MAX = { host: 70, monitor: 80, peripheral: 90 }

export function calcSale(host, monitor, peripheral) {
    // 输入为-1时，表示统计
    if (host === -1) {
        return { type: 'stat', msg: '请输入本月销售数据后再统计' }
    }
    // 校验
    if (host < 1 || host > MAX.host) {
        return { type: 'error', msg: `主机数量必须在1~${MAX.host}之间` }
    }
    if (monitor < 1 || monitor > MAX.monitor) {
        return { type: 'error', msg: `显示器数量必须在1~${MAX.monitor}之间` }
    }
    if (peripheral < 1 || peripheral > MAX.peripheral) {
        return { type: 'error', msg: `外设数量必须在1~${MAX.peripheral}之间` }
    }
    // 计算销售额
    const total = host * UNIT_PRICE.host + monitor * UNIT_PRICE.monitor + peripheral * UNIT_PRICE.peripheral
    // 计算佣金
    let rate = 0.1
    if (total > 1800) rate = 0.2
    else if (total > 1000) rate = 0.15
    const commission = total * rate
    return {
        type: 'success',
        total,
        commission,
        rate,
        msg: `销售额：${total} 元，佣金率：${(rate * 100).toFixed(0)}%，佣金：${commission.toFixed(2)} 元`
    }
}

export default calcSale