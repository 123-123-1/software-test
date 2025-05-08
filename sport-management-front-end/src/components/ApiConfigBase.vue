<script setup>
import { apiConfigInfo, configEnum, createApiConfig, deleteApiConfig, editApiConfig, getApiConfig, getConfigFields, loadPreview } from '@/boundaries/managementBoundary';
import { ref, onMounted } from 'vue';
import { CircleCheckFilled, WarningFilled } from '@element-plus/icons-vue';
import * as monaco from 'monaco-editor';
import JsonWorker from 'monaco-editor/esm/vs/language/json/json.worker?worker'

const props = defineProps({
  configName: String,
  configType: String,
});

const apiEnableSectionEnum = {
  'auto': '自动',
  'manual': '手动',
  'api': '转发请求',
};

const resetEnum = {
  'request': '请求体',
  'response': '返回数据',
  'all': '所有'
};

const requestBodyCodeRef = ref(null);
const requestBodyPreviewRef = ref(null);
const returnDataCodeRef = ref(null);
const returnDataPreviewRef = ref(null);
const deleteConfigDialogVisible = ref(false);
const resetDialog = ref({
  visible: false,
  type: '',
});
const saveState = ref({
  request: true,
  response: true,
});
const editingUrl = ref('');

onMounted(async () => {
  await getApiConfig(props.configName);
  getConfigFields(props.configName);
  editingUrl.value = apiConfigInfo.value.apiUrl;
  initEditor();
});

window.MonacoEnvironment = {
  getWorker: function (moduleId, label) {
    if(label === 'json'){
      return new JsonWorker();
    }
  }
}

let requestEditor, responseDataEditor;

const initEditor = () => {
  const editorOptions = {
    value: '',
    language: 'json',
    automaticLayout: true,
    theme: 'vs',
    tabsize: 2,
  };
  requestEditor = monaco.editor.create(requestBodyCodeRef.value, editorOptions);
  const requestPreview = monaco.editor.create(requestBodyPreviewRef.value, { ...editorOptions, readOnly: true });
  const displaySamplePreview = (e) => {
    checkRequestEdit();
    requestPreview.setValue(loadPreview(requestEditor.getValue(), props.configName, 'requestFields'));
  };
  requestEditor.onDidChangeModelContent(debounce(displaySamplePreview, 1000));
  requestEditor.setValue(apiConfigInfo.value.requestContent);
  if(props.configType === 'api'){
    responseDataEditor = monaco.editor.create(returnDataCodeRef.value, editorOptions);
    const returnDataPreview = monaco.editor.create(returnDataPreviewRef.value, editorOptions);
    const displayReturnPreview = (e) => {
      saveState.value.response = responseDataEditor.getValue() === apiConfigInfo.value.responseContent;
      returnDataPreview.setValue(loadPreview(responseDataEditor.getValue(), props.configName, 'responseFields'));
    }
    responseDataEditor.onDidChangeModelContent(debounce(displayReturnPreview, 1000));
    responseDataEditor.setValue(apiConfigInfo.value.responseContent);
  }
};

const debounce = (func, ms) => {
  let timeout;
  return (...args) => {
    if(timeout){
      clearTimeout(timeout);
    }
    timeout = setTimeout(() => {
      func(...args);
    }, ms);
  };
};

const handleCreateConfig = async () => {
  await createApiConfig(props.configName);
  editingUrl.value = apiConfigInfo.value.apiUrl;
  requestEditor.setValue(apiConfigInfo.value.requestContent);
  responseDataEditor.setValue(apiConfigInfo.value.responseContent);
  saveState.value.request = true;
  saveState.value.response = true;
};

const handleDeleteConfig = async () => {
  await deleteApiConfig(apiConfigInfo.value.apiconfigId);
  deleteConfigDialogVisible.value = false;
};

const showResetDialog = (type) => {
  resetDialog.value.type = type;
  resetDialog.value.visible = true;
};

const resetEdit = () => {
  const { type } = resetDialog.value
  if(type === 'request' || type === 'all'){
    requestEditor.getModel().setValue(apiConfigInfo.value.requestContent);
    if(props.configType === 'api'){
      editingUrl.value = apiConfigInfo.value.apiUrl;
    }
    saveState.value.request = true;
  }
  if(type === 'response' || type === 'all' && props.configType === 'api'){
    responseDataEditor.getModel().setValue(apiConfigInfo.value.responseContent);
    saveState.value.response = true;
  }
  resetDialog.value.visible = false;
  ElMessage.info("已重置编辑内容");
};

const checkRequestEdit = () => {
  saveState.value.request = requestEditor.getValue() === apiConfigInfo.value.requestContent && editingUrl.value === apiConfigInfo.value.apiUrl;
};

const handleSaveConfig = (type) => {
  const configData = {
    apiconfigId: apiConfigInfo.value.apiconfigId
  };
  if(type === 'enable'){
    configData.operationType = apiConfigInfo.value.operationType;
  }
  if(type === 'request' || type === 'all'){
    configData.requestContent = requestEditor.getValue();
    if(props.configType === 'api'){
      configData.apiUrl = editingUrl.value;
    }
    saveState.value.request = true;
  }
  if(type === 'response' || type === 'all' && props.configType === 'api'){
    configData.responseContent = responseDataEditor.getValue();
    saveState.value.response = true;
  }
  editApiConfig(configData);
};

// 未保存数据警告
window.addEventListener('beforeunload', (e) => {
  if(!saveState.value.request || !saveState.value.response){
    e.preventDefault();
  }
});

</script>

<template>
  <div class="apiConfigBase">
    <div class="apiConfigMain">
      <div class="configSection">
        <div class="sectionTitle">配置项策略设置</div>
        <div class="sectionContent">
          <div class="selectionItem">
            <div>{{ configEnum[configName].name }}配置策略：</div>
            <el-select class="apiEnableSelection" v-model="apiConfigInfo.operationType" :disabled="!apiConfigInfo.isCreated" @change="handleSaveConfig('enable')">
              <el-option v-for="selection in Object.keys(apiEnableSectionEnum)"
              :value="selection" :label="apiEnableSectionEnum[selection]"
              :disabled="selection === 'manual' && configType === 'data'"></el-option>
            </el-select>
          </div>
          <div class="selectionInfo" :data-state="apiConfigInfo.operationType">
            {{ configEnum[configName][apiConfigInfo.operationType] }}
          </div>
          <div v-if="!apiConfigInfo.isCreated" class="noCreateMessage">
            暂未创建{{ configEnum[configName].name }}，如果要切换配置策略，请点击下方 创建配置 按钮创建{{ configEnum[configName].name }}配置项
          </div>
        </div>
      </div>
      <div class="configSection">
        <div class="sectionTitle">接口配置说明</div>
        <div class="configInstruction">
          <p>只有配置项策略设置中选择 转发请求 时接口配置才会生效，选择配置项策略为 自动 或 手动 时不需要进行接口配置。</p>
          <p>请求URL地址：用户进行预约等事件时，怡运动系统会自动向该URL地址发送一个请求，URL对应的后端系统可以选择接受或者拒绝预约。请确保该URL的有效性</p>
          <p>接口请求配置：该部分列出了怡运动系统在收到用户事件时会提供的所有字段，您可以使用这些字段配置请求体。请在左侧的输入框中输入配置内容，要求配置格式为JSON格式数据，可以在右侧预览样例数据。要使用某个字段时，使用 ${字段名} 表示在该位置填充字段</p>
          <p>接口返回配置：该部分列出了您的系统在收到请求后应当给出的响应字段，与接口请求字段的配置方法一致。需要确保响应的格式与配置的格式完全一致。</p>
        </div>
      </div>
      <div class="configSection">
        <div class="sectionTitle">接口请求字段</div>
        <el-table :data="configEnum[configName].requestFields" class="requestFieldTable">
          <el-table-column label="字段名" prop="name" width="250"></el-table-column>
          <el-table-column label="描述" prop="description" width="290"></el-table-column>
          <el-table-column label="备注" prop="note"></el-table-column>
          <el-table-column label="类型" prop="type" width="100"></el-table-column>
        </el-table>
      </div>
      <div class="configSection">
        <div class="sectionTitle">请求体配置</div>
        <div class="sectionContent">
          <div class="urlInputArea" v-if="configType === 'api'">
          <div>请求URL地址：</div>
            <el-input v-model="editingUrl" placeholder="输入请求URL" class="urlInputDisplay" @input="(debounce(checkRequestEdit, 1000))()"></el-input>
          </div>
          <div class="codeArea">
            <div class="codeSection">
              <div class="codeSectionTitle">输入配置内容</div>
              <div class="codeEditDisplay" ref="requestBodyCodeRef"></div>
            </div>
            <div class="codeSection">
              <div class="codeSectionTitle">配置结果示例</div>
              <div class="codeEditDisplay" ref="requestBodyPreviewRef"></div>
            </div>
          </div>
          <div class="codeControls">
            <div class="saveTextDisplay" v-if="saveState.request">
              <el-icon><circle-check-filled></circle-check-filled></el-icon>
              <div class="saveInfoText">更改已保存</div>
            </div>
            <div class="unsaveTextDisplay" v-else>
              <el-icon><warning-filled></warning-filled></el-icon>
              <div class="saveInfoText">更改未保存</div>
            </div>
            <el-button type="primary" @click="handleSaveConfig('request')">保存</el-button>
            <el-button @click="showResetDialog('request')">重置</el-button>
          </div>
        </div>
      </div>
      <div class="configSection" v-if="configType === 'api'">
        <div class="sectionTitle">接口返回字段</div>
        <el-table :data="configEnum[configName].responseFields" class="returnFieldTable">
          <el-table-column label="字段名" prop="name" width="250"></el-table-column>
          <el-table-column label="描述" prop="description"></el-table-column>
          <el-table-column label="必填" width="100">
            <template #default="scope">
              <div v-if="scope.row.isRequired">Y</div>
              <div v-else>N</div>
            </template>
          </el-table-column>
          <el-table-column label="类型" prop="type" width="150"></el-table-column>
        </el-table>
      </div>
      <div class="configSection" v-if="configType === 'api'">
        <div class="sectionTitle">返回数据配置</div>
        <div class="codeArea">
          <div class="codeSection">
            <div class="codeSectionTitle">输入配置内容</div>
            <div class="codeEditDisplay" ref="returnDataCodeRef"></div>
          </div>
          <div class="codeSection">
            <div class="codeSectionTitle">配置结果示例</div>
            <div class="codeEditDisplay" ref="returnDataPreviewRef"></div>
          </div>
        </div>
        <div class="codeControls">
          <div class="saveTextDisplay" v-if="saveState.response">
            <el-icon><circle-check-filled></circle-check-filled></el-icon>
            <div class="saveInfoText">更改已保存</div>
          </div>
          <div class="unsaveTextDisplay" v-else>
            <el-icon><warning-filled></warning-filled></el-icon>
            <div class="saveInfoText">更改未保存</div>
          </div>
          <el-button type="primary" @click="handleSaveConfig('response')">保存</el-button>
          <el-button @click="showResetDialog('response')">重置</el-button>
        </div>
      </div>
    </div>
    <div class="apiConfigControls" v-if="apiConfigInfo.isCreated">
      <el-button type="danger" @click="deleteConfigDialogVisible = true">删除配置</el-button>
      <el-button @click="showResetDialog('all')">重置编辑</el-button>
      <el-button type="primary" @click="handleSaveConfig('all')">保存配置</el-button>
    </div>
    <div class="apiConfigControls" v-else>
      <el-button type="primary" @click="handleCreateConfig">创建配置</el-button>
    </div>
  </div>
  <el-dialog v-model="deleteConfigDialogVisible" title="删除确认">
    是否确定删除{{ configEnum[configName].name }}的配置项？
    <template #footer>
      <el-button type="danger" @click="handleDeleteConfig">确定</el-button>
      <el-button @click="deleteConfigDialogVisible = false">取消</el-button>
    </template>
  </el-dialog>
  <el-dialog v-model="resetDialog.visible" title="重置确认">
    确认重置 {{ resetEnum[resetDialog.type] }}配置 的内容吗？未保存的更改会丢失！
    <template #footer>
      <el-button type="primary" @click="resetEdit">确认</el-button>
      <el-button @click="resetDialog.visible = false">取消</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.apiConfigBase{
  display: flex;
  flex-direction: column;
  height: 100%;
}

.apiConfigMain{
  flex: 1;
  overflow: auto;
}

.apiConfigControls{
  display: flex;
  justify-content: flex-end;
  align-items: center;
  background-color: var(--lightgray-bg);
  border: 1px solid var(--lightgray-border);
  padding: 10px;
}

.configSection{
  padding: 10px;
}

.sectionTitle{
  color: var(--theme-darkblue);
  font-size: 1.2em;
  font-weight: 700;
  padding: 10px;
  border-bottom: 1px solid var(--theme-darkblue);
}

.sectionContent{
  padding: 10px;
}

.selectionItem{
  display: flex;
  align-items: center;
}

.apiEnableSelection{
  flex: 1;
}

.selectionInfo{
  font-size: 0.8em;
  line-height: 2em;
  margin-top: 5px;
}

.selectionInfo[data-state="api"]{
  color: var(--lightblue-text);
}

.selectionInfo[data-state="manual"]{
  color: var(--yellow-text);
}

.selectionInfo[data-state="auto"]{
  color: var(--red-text);
}

.noCreateMessage{
  padding-top: 5px;
  padding-bottom: 5px;
  font-weight: 700;
}

.requestFieldTable{
  border: 1px solid var(--lightgray-border);
  margin-top: 10px;
}

.returnFieldTable{
  border: 1px solid var(--lightgray-border);
  margin-top: 10px;
}

.urlInputArea{
  display: flex;
  align-items: center;
  padding: 10px;
}

.urlInputDisplay{
  flex: 1;
}

.codeArea{
  display: flex;
  justify-content: space-evenly;
  margin-top: 10px;
}

.codeSection {
  width: 45%;
}

.codeSectionTitle{
  font-weight: 700;
  line-height: 2em;
  margin-bottom: 5px;
}

.codeControls{
  display: flex;
  align-items: center;
  padding: 10px;
  padding-left: 3.33%;
}

.saveTextDisplay{
  display: flex;
  align-items: center;
  margin-right: auto;
  color: var(--green-text);
}

.unsaveTextDisplay{
  display: flex;
  align-items: center;
  margin-right: auto;
  color: var(--yellow-text);
}

.saveInfoText{
  margin-left: 5px;
}

.codeEditDisplay{
  height: 300px;
  border: 1px solid var(--lightgray-border);
}

.configInstruction{
  padding: 10px;
  color: var(--gray-text);
}

</style>