import {fetchWithDefaultErrorHandlers} from '/script/common.js'
import {restartUpdating, allUsernames} from './update.js'


const chatContent = $('#chat-content')

const contextMenuOverlay = $('#message-contextmenu-overlay')
const contextMenu = $('#message-contextmenu')

const   copyButton = contextMenu.find('[data-action=copy]')
const   editButton = contextMenu.find('[data-action=edit]')
const  replyButton = contextMenu.find('[data-action=reply]')
const deleteButton = contextMenu.find('[data-action=delete]')

let clickedMessageWrapper = null

chatContent.on('contextmenu', '.message-wrapper:not(.deleted)', event => {
	clickedMessageWrapper = $(event.target).closest('.message-wrapper')
	
	if (clickedMessageWrapper.length == 0) {
		clickedMessageWrapper = null
	}
	
	contextMenuOverlay.removeAttr('hidden')
	contextMenu.removeAttr('hidden')
	
	let x = Math.min(event.pageX + 1, document.documentElement.clientWidth - contextMenu.outerWidth() - 1)
	let y = event.pageY + contextMenu.outerHeight() > document.documentElement.clientHeight ?
			event.pageY - contextMenu.outerHeight() - 1 : event.pageY + 1;
	
	contextMenu.css({ left: x, top: y })
	
	if (clickedMessageWrapper.hasClass('own')) {
		editButton.show()
		deleteButton.show()
	} else {
		editButton.hide()
		deleteButton.hide()
	}
	
	event.preventDefault()
})

function hideContextMenu(event) {
	clickedMessageWrapper = null
	
	contextMenuOverlay.attr('hidden', '')
	contextMenu.attr('hidden', '')
	
	if (event.type == 'contextmenu') {
		event.preventDefault()
	}
}

contextMenuOverlay
		.on('click', hideContextMenu)
		.on('contextmenu', hideContextMenu)

contextMenu
		.on('click', hideContextMenu)
		.on('contextmenu', hideContextMenu)

window.onblur = hideContextMenu



function deleteMessage(id) {
	fetchWithDefaultErrorHandlers(`/messages/${id}`, {
		method: 'DELETE'
	},
	restartUpdating)
}

function updateMessage(id, newText) {
	fetchWithDefaultErrorHandlers(`/messages/${id}`, {
		method: 'PATCH',
		body: newText
	},
	restartUpdating)
}


copyButton.click(() => {
	if (clickedMessageWrapper != null) {
		let text = clickedMessageWrapper.children('.message').children('.message-text').text()
		
		if (text != '') {
			navigator.clipboard.writeText(text)
		}
	}
})

deleteButton.click(() => {
	if (clickedMessageWrapper != null) {
		deleteMessage(clickedMessageWrapper.attr('data-id'))
	}
})



const	newMessageText = $('#new-message-text'),
		messageTops = $('.new-message-top'),
		editMessageTop = $('.new-message-top[data-for-action=edit]'),
		replyMessageTop = $('.new-message-top[data-for-action=reply]'),
		
		replyingMessageContainer = replyMessageTop.find('.replying-message-container'),
		replyingMessageOwner   = replyingMessageContainer.find('.replying-message-owner'),
		replyingMessageContent = replyingMessageContainer.find('.replying-message-content'),
		replyingMessageFile    = replyingMessageContainer.find('.replying-message-file')


let editingMessageWrapper = null,
	replyingMessageWrapper = null


function completeEditingOrReplying(clearText) {
	messageTops.attr('hidden', '')
	editingMessageWrapper = null
	replyingMessageWrapper = null
	
	replyingMessageOwner.empty()
	replyingMessageContent.empty()
	replyingMessageFile.empty()
	
	if (clearText || clearText === undefined) {
		newMessageText.val('')
	}
}

function performEditing(event) {
	event.preventDefault()
	updateMessage(editingMessageWrapper.attr('data-id'), newMessageText.val())
	completeEditingOrReplying()
}

function performReplying(event) {
	event.replyFor = replyingMessageWrapper.attr('data-id')
	completeEditingOrReplying(false)
}


editButton.click(() => {
	if (clickedMessageWrapper != null) {
		completeEditingOrReplying()
		
		editMessageTop.removeAttr('hidden')
		newMessageText.val(clickedMessageWrapper.children('.message').children('.message-text').text()).focus()
		editingMessageWrapper = clickedMessageWrapper
	}
})


const	audioFileIcon = $('#data .audio-file-icon'),
		videoFileIcon = $('#data .video-file-icon'),
		fileIcon      = $('#data .file-icon')


replyButton.click(() => {
	if (clickedMessageWrapper != null) {
		completeEditingOrReplying()
		
		let authorId = clickedMessageWrapper.attr('data-author-id')
		
		$('<a class="message-author">').attr('href', `/users/${authorId}`).text(allUsernames[authorId]).appendTo(replyingMessageOwner)
		
		clickedMessageWrapper.children('.message').children('.message-text').clone().appendTo(replyingMessageContent)
		
		let fileElement = clickedMessageWrapper.children('.message').children('.message-file')
		
		if (fileElement.length != 0) {
			switch (fileElement.attr('data-file-type')) {
				case 'image': replyingMessageFile.append(fileElement.clone()); break
				case 'audio': replyingMessageFile.append(audioFileIcon); break
				case 'video': replyingMessageFile.append(videoFileIcon); break
				default:      replyingMessageFile.append(fileIcon)
			}
		}
		
		replyMessageTop.removeAttr('hidden')
		newMessageText.focus()
		replyingMessageWrapper = clickedMessageWrapper
	}
})

messageTops.find('.close-button').click(() => completeEditingOrReplying())



$('#submit-new-message').click(event => {
	if (editingMessageWrapper != null) {
		performEditing(event)
	} else if (replyingMessageWrapper != null) {
		performReplying(event)
	}
})


newMessageText.on('keypress', event => {
	if (event.key == 'Enter' && !event.shiftKey) {
		if (editingMessageWrapper != null) {
			performEditing(event)
		} else if (replyingMessageWrapper != null) {
			performReplying(event)
		}
	}
})

newMessageText.on('keydown', event => {
	if ((editingMessageWrapper != null || replyingMessageWrapper != null) && event.key == 'Escape' && !event.shiftKey) {
		event.preventDefault()
		completeEditingOrReplying()
	}
})

