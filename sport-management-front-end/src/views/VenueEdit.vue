<script setup>
import {
  editingVenue, initVenueEdit, editResultDialog, editVenueBasicInfo, resetBasicInfo,
  resetCourtInfo, editCourtInfo, createCourt, uploadImageDialog, uploadVenueImage,
  deleteCourtDialog, showCourtDelete, deleteCourt
} from '@/boundaries/managementBoundary';
import { courtStateEnum, venueStateEnum } from '@/boundaries/venueBoundary';
import { ref, onMounted } from 'vue';

const venueImageInputRef = ref(null);

const handleImageChange = () => {
  if(!venueImageInputRef.value.files || venueImageInputRef.value.files.length === 0){
    return;
  }
  uploadImageDialog.value.visible = true;
  uploadImageDialog.value.targetFile = venueImageInputRef.value.files[0];
  if(uploadImageDialog.value.previewUrl){
    URL.revokeObjectURL(uploadImageDialog.value.previewUrl);
  }
  uploadImageDialog.value.previewUrl = URL.createObjectURL(uploadImageDialog.value.targetFile);
  venueImageInputRef.value.files = null;
};

onMounted(() => {
  initVenueEdit();
});

</script>

<template>
  <div class="venueEdit">
    <div class="infoBlock">
      <div class="infoBlockTitle">基本信息</div>
      <div class="basicInfoArea">
        <div class="basicInfoText">
          <div class="basicInfoItem">
            <div class="basicInfoLabel">场馆名称</div>
            <el-input class="textInputDisplay" v-model="editingVenue.venueName" placeholder="输入场馆名称"></el-input>
          </div>
          <div class="basicInfoItem">
            <div class="basicInfoLabel">简介</div>
            <el-input class="textInputDisplay" v-model="editingVenue.description" placeholder="输入场馆简介"></el-input>
          </div>
          <div class="basicInfoItem">
            <div class="basicInfoLabel">编号</div>
            <div class="basicInfoContent">{{ editingVenue.venueId }}</div>
          </div>
          <div class="basicInfoItem">
            <div class="basicInfoLabel">地址</div>
            <el-input class="textInputDisplay" v-model="editingVenue.location" placeholder="输入场馆地址"></el-input>
          </div>
          <div class="basicInfoItem">
            <div class="basicInfoLabel">联系电话</div>
            <el-input class="textInputDisplay" v-model="editingVenue.contactNumber" placeholder="输入联系电话"></el-input>
          </div>
          <div class="basicInfoItem">
            <div class="basicInfoLabel">开放状态</div>
            <el-select class="textInputDisplay" v-model="editingVenue.state">
              <el-option v-for="stateOption in Object.keys(venueStateEnum)" :value="stateOption"
              :label="venueStateEnum[stateOption]"></el-option>
            </el-select>
          </div>
          <div class="basicInfoControls">
            <el-button type="primary" @click="editVenueBasicInfo">保存更改</el-button>
            <el-button @click="resetBasicInfo">重置信息</el-button>
          </div>
        </div>
        <div class="basicInfoImage">
          <img class="venueImage" :src="editingVenue.image" />
          <div class="imageControls">
            <el-button type="primary" @click="venueImageInputRef.click()">上传图片</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div class="infoBlock">
    <div class="infoBlockTitle">场地信息</div>
    <el-table :data="editingVenue.courts" :show-overflow-tooltip="{ effect: 'light'}">
      <el-table-column label="编号" prop="courtId" width="100"></el-table-column>
      <el-table-column label="名称">
        <template #default="scope">
          <el-input v-if="scope.row.isEdit" placeholder="场地名称" v-model="scope.row.courtName"></el-input>
          <div v-else>{{ scope.row.courtName }}</div>
        </template>
      </el-table-column>
      <el-table-column label="容量" width="180">
        <template #default="scope">
          <el-input-number v-if="scope.row.isEdit" v-model="scope.row.capacity"></el-input-number>
          <div v-else>{{ scope.row.capacity }}</div>
        </template>
      </el-table-column>
      <el-table-column label="类型" width="150">
        <template #default="scope">
          <el-input v-if="scope.row.isEdit" v-model="scope.row.type"></el-input>
          <div v-else>{{ scope.row.type }}</div>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="state">
        <template #default="scope">
          <el-select v-if="scope.row.isEdit" v-model="scope.row.state">
            <el-option v-for="stateOption in Object.keys(courtStateEnum)" :value="stateOption" :label="courtStateEnum[stateOption]"></el-option>
          </el-select>
          <div v-else class="venueState" :data-state="scope.row.state">{{ courtStateEnum[scope.row.state] }}</div>
        </template>
      </el-table-column>
      <el-table-column label="位置">
        <template #default="scope">
          <el-input v-if="scope.row.isEdit" v-model="scope.row.location" placeholder="场地位置"></el-input>
          <div v-else>{{ scope.row.location }}</div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140">
        <template #default="scope">
          <div v-if="scope.row.isEdit">
            <el-button size="small" type="primary" @click="editCourtInfo(scope.$index)">保存</el-button>
            <el-button size="small" @click="resetCourtInfo(scope.$index)">取消</el-button>
          </div>
          <div v-else>
            <el-button size="small" type="primary" @click="scope.row.isEdit = true">编辑</el-button>
            <el-button size="small" type="danger" @click="showCourtDelete(scope.$index)">删除</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>
    <div class="courtCreateButton">
      <el-button type="primary" @click="createCourt()">+ 添加场地</el-button>
    </div>
  </div>
  <el-dialog v-model="editResultDialog.visible" :title="editResultDialog.title">
    {{ editResultDialog.message }}
    <template #footer>
      <el-button type="primary" @click="editResultDialog.visible = false">确定</el-button>
    </template>
  </el-dialog>
  <el-dialog v-model="deleteCourtDialog.visible" title="删除场地确认">
    <div class="deleteCourtText">
      <div>确认要删除场地 {{ editingVenue.courts[deleteCourtDialog.targetIndex]?.courtName }} (ID: {{ editingVenue.courts[deleteCourtDialog.targetIndex]?.courtId }}) 吗？</div>
      <div>删除前请确认没有与该场地关联的开放时间段，否则会导致删除失败！</div>
    </div>
    <template #footer>
      <el-button type="danger" @click="deleteCourt">确定</el-button>
      <el-button @click="deleteCourtDialog.visible = false">取消</el-button>
    </template>
  </el-dialog>
  <el-dialog v-model="uploadImageDialog.visible" title="上传图片" align-center >
    <div class="imagePreviewArea">
      <img class="venueImagePreview" :src="uploadImageDialog.previewUrl" />
    </div>
    <template #footer>
      <el-button type="primary" @click="uploadVenueImage">上传</el-button>
      <el-button @click="uploadImageDialog.visible = false">取消</el-button>
    </template>
  </el-dialog>
  <input type="file" ref="venueImageInputRef" v-show="false" @change="handleImageChange" />
</template>

<style scoped>
.infoBlock{
  background-color: var(--lightgray-bg);
  margin-bottom: 10px;
  border-radius: 5px;
  border: 1px solid var(--lightgray-border);
  box-shadow: 0px 2px 4px  rgba(0, 0, 0, 0.25);
  margin: 10px;
}

.infoBlockTitle{
  font-size: var(--title-size);
  padding: 10px;
  font-weight: 700;
  color: var(--theme-darkblue);
  background-color: var(--lightblue-bg);
  border-bottom: 1px solid var(--theme-darkblue);
}

.basicInfoArea{
  display: flex;
  justify-content: space-evenly;
  padding: 10px;
}

.basicInfoText{
  width: 45%;
  margin-left: 10px;
  padding-left: 10px;
  padding-right: 10px;
}

.basicInfoImage{
  width: 45%;
}

.venueImage{
  width: min(100%, 480px);
  height: 300px;
}

.venueTitle{
  font-size: 18px;
  padding-top: 10px;
  padding-bottom: 10px;
  font-weight: 700;
}

.basicInfoItem{
  display: flex;
  align-items: center;
  padding-top: 10px;
  padding-bottom: 10px;
}

.basicInfoLabel{
  width: 100px;
}

.basicInfoContent{
  display: flex;
  align-items: center;
  color: var(--gray-text);
}

.textInputDisplay{
  flex: 1;
}

.basicInfoControls{
  display: flex;
  justify-content: space-evenly;
  padding-top: 10px;
  padding-bottom: 10px;
}

.imageControls{
  display: flex;
  justify-content: center;
  padding: 10px;
}

.venueState[data-state="open"]{
  color: var(--green-text);
}

.venueState[data-state="closed"]{
  color: var(--red-text);
}

.courtCreateButton{
  padding: 5px;
  color: var(--lightgray-bg);
}

.courtCreateButton>button{
  width: 100%;
}

.imagePreviewArea{
  display: flex;
}

.venueImagePreview{
  margin-left: auto;
  margin-right: auto;
  max-width: 100%;
  max-height: 75vh;
}

.deleteCourtText{
  font-size: 1.1em;
  line-height: 2em;
}

</style>