import {fetchWithDefaultErrorHandlers, $findOrAppendChild} from '/script/common.js'


const UPDATE_INTERVAL = 5000


const main = $('main')
const chatContent = $('#chat-content')

const warningElement = $('#warning')
const warningContent = warningElement.find('.warning-content')
const networkErrorMessage = $('#data .network-error-message').text()

warningElement.find('.close-button').click(() => void main.removeClass('warning-shown'))


/**
 * Приводит дату к строке в формате "yyyy-MM-dd hh:mm:ss"
 * @param {Date} date - дата для форматирования
 * @return {string} Отформатированную дату
 */
function formatDate(date) { // yyyy-MM-dd hh:mm:ss
	return	(date.getFullYear() ).toString().padStart(4, '0') + '-' +
			(date.getMonth() + 1).toString().padStart(2, '0') + '-' +
			(date.getDate()     ).toString().padStart(2, '0') + ' ' +
			(date.getHours()    ).toString().padStart(2, '0') + ':' +
			(date.getMinutes()  ).toString().padStart(2, '0') + ':' +
			(date.getSeconds()  ).toString().padStart(2, '0')
}

const TIME_FORMAT_OPTIONS = { hour: 'numeric', minute: 'numeric' }

const WEEKDAY_DATE_FORMAT_OPTIONS = { weekday: 'long' }
const THIS_YEAR_DATE_FORMAT_OPTIONS = { month: 'long', day: 'numeric' }
const OTHER_YEAR_DATE_FORMAT_OPTIONS = { month: 'long', day: 'numeric', year: 'numeric' }


const todayMessage = $('#data .today-message').text()
const yesterdayMessage = $('#data .yesterday-message').text()

/**
 * Приводит дату к строке типа "Сегодня", "Вчера", "Среда" и т.д.
 * @param {Date} date - дата для форматирования
 * @return {string} Отформатированную дату
 */
function dateToPrettyString(date) {
	let today = new Date()
	
	const millisecondsInDay = 24*60*60*1000
	
	today.setHours(0, 0, 0, 0)
	
	let daysDifference = Math.floor((today.getTime() - date.getTime()) / millisecondsInDay)
	
	switch (daysDifference) {
		case 0: return todayMessage
		case 1: return yesterdayMessage
		default:
			let formatOptions = daysDifference <= 6 ? WEEKDAY_DATE_FORMAT_OPTIONS :
					today.getFullYear() == date.getFullYear() ? THIS_YEAR_DATE_FORMAT_OPTIONS : OTHER_YEAR_DATE_FORMAT_OPTIONS
			
			
			return date.toLocaleDateString(navigator.language, formatOptions)
	}
}


function scrollToBottom() {
	chatContent.animate({ scrollTop: chatContent.get(0).scrollHeight }, 100)
}


/**
 * Запрашивает у сервера список изменений и возвращает их.
 * @param {Date} since - время последней проверки
 * @callback onSuccess(response) - функция, которая вызывается при успешном запросе
 * @return {Promise} Промис, возвращённый fetch
 */
function getMessages(since) {
	let url = new URL(location.pathname + '/changes', location.origin)
	url.searchParams.append('since', formatDate(since))
	
	if (since.getTime() != 0) {
		url.searchParams.append('include-edited-and-deleted', '')
	}
	
	return fetchWithDefaultErrorHandlers(url, {
				redirect: 'manual'
			},
			response => {
				 // Если срок действия куки авторизации истёк, сервер вернёт редирект на страницу входа
				if (response.redirected) {
					location.replace(response.url)
					return undefined
				} else {
					return response.json()
				}
			},
			null,
			error => {
				if (error.name == 'NetworkError' ||
					error.name == 'TypeError' && error.message.startsWith('NetworkError')) {
					
					warningContent.text(networkErrorMessage)
					main.addClass('warning-shown')
					
					stopUpdating()
					
				} else {
					return true // Обработчик по умолчанию
				}
			}
	)
}


let lastUpdateTime = new Date(0)

let lastMessageAuthorId = null
let lastMessageDate = lastUpdateTime // ero date


const loadedMessageIds = []
export const allUsernames = {}


/**
 * Обновляет сообщения. Запрашивает у сервера список изменений и затем отображает их на странице
 */
async function updateMessages(firstTime) {
	let prevUpdateTime = lastUpdateTime
	lastUpdateTime = new Date()
	
	let data = await getMessages(prevUpdateTime)
	
	if (typeof data != 'object' || data == null) {
		return false
	}
	
	let usernames = data.usernames
	Object.assign(allUsernames, usernames)
	
	
	data.messages.forEach(message => message.publicationTime = new Date(message.publicationTime))
	
	data.messages
			.sort((message1, message2) => message1.publicationTime - message2.publicationTime)
			.forEach(message => {
				let messageId = message.id
				
				if (loadedMessageIds.includes(messageId)) {
					return
				}
				
				loadedMessageIds.push(messageId)
				
				
				let publicationTime = message.publicationTime
				
				let publicationDate = new Date(publicationTime)
				publicationDate.setHours(0, 0, 0, 0)
				
				if (lastMessageDate.getTime() != publicationDate.getTime()) {
					lastMessageDate = publicationDate
					$('<div class="message-date">').text(dateToPrettyString(publicationDate)).appendTo(chatContent)
				}
				
				
				let authorId = message.authorId
				
				
				let messageWrapper = $('<div class="message-wrapper">').attr('data-id', messageId).attr('data-author-id', authorId)
				let messageElem = $('<div class="message">').appendTo(messageWrapper)
				
				let replyFor = message.replyFor
				
				if (replyFor != null) {
					let replyingMessageWrapper = $(`.message-wrapper[data-id=${replyFor}]`)
					
					if (replyingMessageWrapper.length != 0) {
						let replyingMessageAuthorId = replyingMessageWrapper.attr('data-author-id')
						
						let messageReply = $('<div class="message-reply">').appendTo(messageElem)
								.append('<span class="message-reply-icon">')
								.append($('<a class="message-author">').attr('href', `/users/${replyingMessageAuthorId}`).text(usernames[replyingMessageAuthorId]))
								.append(replyingMessageWrapper.children('.message').children('.message-text,.message-file').clone())
					}
				}
				
				
				if (authorId != lastMessageAuthorId) {
					if (authorId != data.currentUserId) {
						let linkToAuthor = `/users/${authorId}`
						
						$('<a class="picture">').attr('href', linkToAuthor).html(`<img src="/users/${authorId}/picture">`).appendTo(messageWrapper)
						
						$('<a class="message-author">').attr('href', linkToAuthor).text(usernames[authorId]).appendTo(messageElem)
					}
					
					lastMessageAuthorId = authorId
					messageWrapper.addClass('first')
				}
				
				if (authorId == data.currentUserId) {
					messageWrapper.addClass('own')
				}
				
				
				if (message.text != '') {
					$('<pre class="message-text">').text(message.text).appendTo(messageElem)
				}
				
				
				let file = message.file
				
				if (file != null) {
					let fileElem
					
					switch (file.type) {
						case 'image':
							fileElem = $('<img class="message-media-file">').attr('alt', file.name)
							messageElem.addClass('image')
							
							if (firstTime) {
								fileElem.on('load', scrollToBottom)
							}
							
							break
						
						case 'video': case 'audio':
							
							fileElem = $(`<${file.type} class="message-media-file" controls>`)
							
							if (firstTime) {
								fileElem.on('loadedmetadata', scrollToBottom)
							}
							
							break
						
						default:
							fileElem = $('<a download>').text(file.name)
					}
					
					let isMediaFile = fileElem.prop('tagName') != 'A'
					
					fileElem.addClass('message-file')
							.attr('data-file-type', file.type)
							.attr(isMediaFile ? 'src' : 'href', `/messages/files/${messageId}`)
							.appendTo(messageElem)
				}
				
				messageElem.append(`<div class="message-time">${message.publicationTime.toLocaleTimeString(navigator.language, TIME_FORMAT_OPTIONS)}</div>`)
				
				chatContent.append(messageWrapper)
			})
	
	
	if (data.edited != null) {
		data.edited.forEach(message => {
			let messageElem = $(`.message-wrapper[data-id=${message.id}] .message`)
			
			$findOrAppendChild(messageElem, '.message-text', '<div class="message-text">').text(message.text)
			
			if (messageElem.children('.message-edited-notice').length == 0) {
				messageElem.append($('#data .message-edited-notice').clone())
			}
		})
	}
	
	if (data.deleted != null) {
		data.deleted.forEach(messageId => {
			let messageElem = $(`.message-wrapper[data-id=${messageId}] .message`)
			let messageAuthor = messageElem.children('.message-author')
			
			messageElem.empty().addClass('deleted')
					.append(messageAuthor)
					.append($('#data .message-deleted-notice').clone())
		})
	}
	
	
	let chatContentElem = chatContent.get(0)
	
	if (firstTime) {
		scrollToBottom()
		
	} else if (chatContentElem.scrollTop >= chatContentElem.scrollHeight - chatContentElem.offsetHeight) {
		scrollToBottom()
	}
	
	
	return true
}



let intervalId

/**
 * Запускает обновление сообщений. Функция updateMessages запускается с промежутком UPDATE_INTERVAL.
 * Если обновление сообщений уже запущено, то ничего не делает
 */
export function startUpdating(firstTime) {
	if (intervalId !== undefined) {
		return
	}
	
	intervalId = setInterval(
		() => {
			if (!updateMessages()) {
				stopUpdating()
			}
		},
		UPDATE_INTERVAL
	)
	
	updateMessages(firstTime)
}

/**
 * Останавливает обновление сообщений
 */
export function stopUpdating() {
	clearInterval(intervalId)
	intervalId = undefined
}

/**
 * Перезапускает обновление сообщений
 */
export function restartUpdating() {
	stopUpdating()
	startUpdating()
}


window.onfocus = startUpdating
window.onblur = stopUpdating

startUpdating(true)

$(window).on('load', () => {
	chatContent.removeAttr('data-loading')
})

