<script setup>
import {
  messageInput, selectChat, selectedChat, userChatList, postChatMessage, getUserChats,
  chatMessages, getUserFriends, userFriendList
} from '@/boundaries/socializationBoundary'
import { ref, onMounted } from 'vue';
import useUserStore from '@/stores/userStore';
import { storeToRefs } from 'pinia';
import { useRoute } from 'vue-router';
import AddUserDialog from '@/components/AddUserDialog.vue';
import CreateChatDialog from '@/components/CreateChatDialog.vue';
import { useMenuStore } from '@/stores/menuStore';

const userStore = useUserStore();
const { userId } = storeToRefs(userStore);
const menuStore = useMenuStore();

const route = useRoute();
const addUserDialogRef = ref(null);
const createChatRef = ref(null);
const chatType = ref('');

onMounted(async () => {
  chatType.value = route.query.type;
  if(chatType.value === 'group'){
    menuStore.updateMenuByIndex('chats-group');
    await getUserChats();
  }
  else if(chatType.value === 'friend'){
    menuStore.updateMenuByIndex('chats-friends');
    await getUserFriends();
  }
  if(route.query.id){
    let targetChat = null;
    if(chatType.value === 'group'){
      targetChat = userChatList.value.find(chat => chat.chatId.toString() === route.query.id); 
    }
    else if(chatType.value === 'friend'){
      targetChat = userFriendList.value.find(chat => chat.chatId.toString() === route.query.id);
    }
    if(targetChat){
      selectChat(targetChat);
    }
  }
  else{
    selectChat(null);
  }
});

</script>

<template>
  <div class="chatList">
    <div class="listArea">
      <div class="chatItemList">
        <div class="chatItem" v-for="chat in userChatList" :data-selected="chat.chatId === selectedChat?.chatId"
          @click="selectChat(chat)">
          <img class="chatAvatar" v-if="chat.type === 'friendChat'" :src="chat.photo" />
          <div class="chatType" v-if="chat.type === 'groupChat'">[团体]</div>
          <div class="chatType" v-else-if="chat.type === 'matchChat'">[拼场]</div>
          <div class="chatName">{{ chat.chatName }}</div>
        </div>
      </div>
      <div class="chatListControls">
        <el-button v-if="chatType === 'friend'" @click="addUserDialogRef.showSearchUserDialog()"
        size="large" type="primary">添加好友</el-button>
        <el-button v-else-if="chatType === 'group'" @click="createChatRef.showChatCreate()"
        size="large" type="primary">创建群聊</el-button>
      </div>
    </div>
    <div class="chatArea" v-if="selectedChat">
      <div class="chatHeaderArea">
        <div class="chatType" v-if="selectedChat.type === 'groupChat'">[团体]</div>
        <div class="chatType" v-else-if="selectedChat.type === 'matchChat'">[拼场]</div>
        <div class="chatName">{{ selectedChat.chatName }}</div>
      </div>
      <div class="messageArea">
        <div class="messageItem" v-for="message in chatMessages">
          <div class="receivedMessage" v-if="message.userId !== userId">
            <img class="chatAvatar" :src="message.photo" />
            <div class="messageMain">
              <div class="chatUserName">{{ message.userName }}</div>
              <div class="messageContent">{{ message.content }}</div>
            </div>
          </div>
          <div class="sentMessage" v-else>
            <div class="messageContent">{{ message.content }}</div>
            <img class="chatAvatar" :src="message.photo" />
          </div>
        </div>
      </div>
      <div class="sendArea">
        <el-input placeholder="发送消息..." v-model="messageInput"></el-input>
        <el-button type="primary" @click="postChatMessage">发送</el-button>
      </div>
    </div>
  </div>
  <add-user-dialog ref="addUserDialogRef"></add-user-dialog>
  <create-chat-dialog ref="createChatRef"></create-chat-dialog>
</template>

<style scoped>
.chatList{
  flex: 1;
  display: flex;
}

.listArea{
  display: flex;
  flex-direction: column;
  height: calc(100vh - var(--top-size) - 22px);
  width: 30%;
  border-right: 1px solid var(--gray-border);
  border-bottom: 1px solid var(--gray-border);
}

.chatArea{
  display: flex;
  flex-direction: column;
  width: 70%;
  height: calc(100vh - var(--top-size) - 22px);
  border-bottom: 1px solid var(--gray-border);
}

.chatItem{
  display: flex;
  align-items: center;
  padding: 10px;
  background-color: var(--lightgray-bg);
  border-bottom: 1px solid var(--lightgray-border);
}

.chatItem:hover{
  cursor: pointer;
  background-color: var(--gray-bg);
}

.chatItem[data-selected=true]{
  background-color: var(--lightblue-bg);
}

.chatItemList{
  flex: 1;
  overflow: auto;
}

.chatListControls{
  padding: 5px;
}

.chatListControls>button{
  width: 100%;
}

.chatAvatar{
  width: 40px;
  height: 40px;
  margin-right: 10px;
}

.chatType{
  color: var(--lightblue-text);
  margin-right: 5px;
}

.chatHeaderArea{
  display: flex;
  background-color: var(--lightgray-bg);
  border-bottom: 1px solid var(--lightgray-border);
  padding: 10px;
}

.messageArea{
  flex: 1;
}

.sendArea{
  display: flex;
  border-top: 1px solid var(--lightgray-border);
  background-color: var(--lightgray-bg);
  padding: 10px;
}

.receivedMessage{
  display: flex;
  align-items: center;
  padding: 10px;
}

.sentMessage{
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 10px;
}

.sentMessage>.chatAvatar{
  margin-right: 0;
  margin-left: 10px;
}

.sentMessage>.messageContent{
  background-color: var(--lightblue-bg);
}

.chatUserName{
  font-size: 12px;
  margin-bottom: 5px;
}

.messageContent{
  border: 1px solid var(--lightgray-border);
  max-width: 500px;
  white-space: wrap;
  padding-top: 5px;
  padding-bottom: 5px;
  padding-left: 10px;
  padding-right: 10px;
  border-radius: 5px;
}


</style>