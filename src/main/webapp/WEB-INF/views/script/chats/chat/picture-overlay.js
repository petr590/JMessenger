const picturePopup = $('#picture-popup')

function hidePopup() {
	picturePopup.attr('hidden', '')
}

picturePopup.click(event => {
	if (event.target == picturePopup.get(0)) {
		hidePopup()
	}
})

picturePopup.find('.close-button').click(hidePopup)

$(document).on('keydown', event => {
	if (event.key == 'Escape') {
		hidePopup()
	}
})


const picture = picturePopup.find('.popup-content')

$('#chat-content').on('click', '.message.image .message-file', event => {
	picturePopup.removeAttr('hidden')
	picture.attr('src', event.target.src)
})
