'use strict'
$(() => {
	const MESSAGE_URL = new URL(location.pathname + '/message', location.origin)
	
	const newMessageTextarea = $('#new-message')
	
	
	function updateNewMessageTextareaSize() {
		let matches = newMessageTextarea.val().match(/\n/g)
		newMessageTextarea.attr('rows', (matches == null ? 1 : matches.length + 1))
	}
	
	updateNewMessageTextareaSize()
	
	newMessageTextarea.on('input', updateNewMessageTextareaSize)
	
	
	
	function sendMessage(text) {
		return fetch(MESSAGE_URL, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify({
				text: text
			})
		}).then(async response => {
			if (response.ok) {
				
			} else {
				let text = await response.text()
				alert(`HTTP Error: ${response.status} ${response.textStatus}${text == '' ? '' : ': '}${text}`)
			}
			
		}).catch(error => {
			alert(`Error: ${error.message}`)
			console.log(error)
		})
	}
	
	
	newMessageTextarea.on('keypress', event => {
		if (event.key == 'Enter' && !event.shiftKey) {
			event.preventDefault()
			
			let text = newMessageTextarea.val()
			if (text.match(/^\s*$/) == null) {
				sendMessage(text).then(() => newMessageTextarea.val(''))
			}
		}
	})
})
