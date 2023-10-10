document.body.addEventListener('htmx:beforeOnLoad', event => {
	let detail = event.detail
	
	if (detail.xhr.status === 422) {
		detail.shouldSwap = true
	}
})
