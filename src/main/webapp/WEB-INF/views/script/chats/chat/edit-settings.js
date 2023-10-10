const chatPictureInput = $('#chat-picture-input')

const pictureSrc = chatPictureInput.parent().find('img').attr('src')

document.body.addEventListener('htmx:afterRequest', event => {
	if (event.detail.successful && event.explicitOriginalTarget == chatPictureInput.get(0)) {
		let newSrc = pictureSrc + '?t=' + Date.now()
		$(`img[src^='${pictureSrc}']`).attr('src', newSrc)
	}
})
