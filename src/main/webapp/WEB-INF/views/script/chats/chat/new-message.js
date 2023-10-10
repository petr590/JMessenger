import {fetchWithDefaultErrorHandlers} from '/script/common.js'
import {restartUpdating} from './update.js'

const newMessageTextarea = $('#new-message-text')


function updateNewMessageTextareaSize() {
	let matches = newMessageTextarea.val().match(/\n/g)
	newMessageTextarea.attr('rows', (matches == null ? 1 : matches.length + 1))
}

updateNewMessageTextareaSize()

newMessageTextarea.on('input', updateNewMessageTextareaSize)



const MESSAGE_URL = new URL(location.pathname + '/message', location.origin)

const fileTooBigMessageElem = $('#data .file-too-big-message')
const fileTooBigMessage = fileTooBigMessageElem.text()
const maxFileSize = parseInt(fileTooBigMessageElem.attr('data-max-file-size'))

function sendMessage(text, replyFor, file, onSuccess) {
	let formData = new FormData()
	
	if (text != '') {
		// Без указания кодировки браузер кодирует текст в непонятную кодировку, которую не может распознать сервер
		formData.append('text', new Blob([text], { type: 'text/plain; charset=utf-8' }))
	}
	
	if (replyFor != null) {
		formData.append('replyFor', replyFor)
	}
	
	/* Firefox выдаёт NetworkError с файлами больше 11MB, поэтому нужен такой костыль */
	if (file.size > 10 * 1024 * 1024 ||
		file.size > maxFileSize) {
		
		alert(fileTooBigMessage)
		return
	}
	
	if (file != null) {
		formData.append('file', file)
		console.log(file, file instanceof Blob)
	}
	
	return fetchWithDefaultErrorHandlers(MESSAGE_URL, {
				method: 'POST',
				body: formData
			},
			
			onSuccess,
			
			response => {
				if (response.status == 413) {
					alert(fileTooBigMessage)
				} else {
					return true // Вывести стандартное сообщение об ошибке
				}
			}
	)
}


function sendMessageIfNotEmpty(replyFor, file, onSuccess) {
	let text = newMessageTextarea.val()
	
	if (text.match(/^\s*$/) == null || file != null) {
		newMessageTextarea.val('')
		return sendMessage(text, replyFor, file, restartUpdating)
	}
}


newMessageTextarea.on('keypress', event => {
	if (event.key == 'Enter' && !event.shiftKey && !event.isDefaultPrevented()) {
		event.preventDefault()
		sendMessageIfNotEmpty(event.replyFor)
	}
})

$('#submit-new-message').removeAttr('disabled').click(event => {
	if (!event.isDefaultPrevented()) {
		sendMessageIfNotEmpty(event.replyFor)
	}
})


const chatContent = $('#chat-content')
let dragEntered = false

function enterDrag() {
	dragEntered = true
	chatContent.addClass('drag')
			.get(0).style.setProperty('--drag-top', chatContent.scrollTop() + 'px')
}

function exitDrag() {
	dragEntered = false
	chatContent.removeClass('drag')
}


chatContent.on('dragover', event => event.preventDefault())

chatContent.on('dragenter', event => {
	event.preventDefault()
	
	if (!dragEntered && $(event.target).closest('#chat-content').length != 0) {
		enterDrag()
	}
})

chatContent.on('dragexit', event => {
	event.preventDefault()
	
	if (dragEntered && event.target == chatContent.get(0)) {
		exitDrag()
	}
})



chatContent.on('drop', event => {
	event.preventDefault()
	exitDrag()
	
	for (let file of event.originalEvent.dataTransfer.files) {
		sendMessageIfNotEmpty(event.replyFor, file)
	}
})

