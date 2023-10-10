import {fetchWithDefaultErrorHandlers} from '/script/common.js'


const MIN_VALUE_LENGTH = 1
const REQUEST_SIZE = 128
let nextPage = 0

const searchForm = $('#search-form')
const searchInput = searchForm.find('input[name=value]')
const searchResults = $('#search-results')
const searchNext = $('#search-next')


function resetSearchResults() {
	searchResults.empty()
	nextPage = 0
}


async function searchNextChat() {
	let value = searchInput.val()
	
	if (value.length >= MIN_VALUE_LENGTH) {
		searchResults.attr('data-state', 'loading')
		
		let url = new URL('/chats/search', location.origin)
		
		url.search = searchForm.serialize()
		url.searchParams.append('size', REQUEST_SIZE)
		url.searchParams.append('page', nextPage)
		
		let chats = await fetchWithDefaultErrorHandlers(url, {}, response => response.json())
		
		for (let chat of chats) {
			searchResults.append(
					$('<div class="list-item"></div>')
							.append(`<div class="picture"><img src="/chats/${chat.id}/picture"></div>`)
							.append($(`<a href="/chats/${chat.id}"></a>`).text(chat.title))
			)
		}
		
		if (chats.length >= REQUEST_SIZE) {
			searchNext.removeAttr('hidden')
		} else {
			searchNext.attr('hidden', '')
		}
		
		searchResults.attr('data-state', 'loaded')
		
		nextPage += 1
	}
}



searchForm.on('submit', event => {
	event.preventDefault()
	resetSearchResults()
	searchNextChat()
})

searchNext.click(() => {
	searchNextChat()
})

searchForm.find('[type=reset]').click(() => {
	searchInput.focus()
})

