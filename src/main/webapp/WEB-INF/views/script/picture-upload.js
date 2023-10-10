$('.uploadable-picture').click(event => {
	$(event.target).find('.picture-input').click()
})

$('.uploadable-picture .picture-input').on('change', event => {
	let file = event.target.files[0]
	
	if (file != null) {
		let reader = new FileReader()
		
		reader.onload = loadEvent => $(event.target).closest('.uploadable-picture').find('img').attr('src', loadEvent.target.result)
		
		reader.readAsDataURL(file)
    }
})
