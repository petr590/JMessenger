const main = $('main')

$('#chat-header .picture, .members-count').click(() => void main.toggleClass('chat-settings-shown'))

$('#chat-settings').find('.close-button').click(() => void main.removeClass('chat-settings-shown'))
