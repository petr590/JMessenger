$(() => {
	let editProfileButton = $('[action=edit-profile]'),
		saveProfileButton = $('[action=save-profile]'),
		usernameElement = $('#username'),
		statusElement = $('.status')
	
	
	async function saveProfile(profile) {
		let url = new URL(location)
		url.search = ''
		url.hash = ''
		
		try {
			let response = await fetch(url, {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify(profile)
			})
			
			if (response.ok) {
				statusElement.text('Профиль успешно сохранён')
				
			} else {
				statusElement.addClass('error')
				statusElement.text(`Не удалось сохранить профиль: HTTP error: ${response.status} ${response.statusText}`);
				
				console.error(`HTTP error: ${response.status} ${response.statusText}`);
			}
			
		} catch (err) {
			statusElement.addClass('error')
			statusElement.text(`Не удалось сохранить профиль: ${err.message}`)
		}
	}
	
	

	editProfileButton.click(() => {
		editProfileButton.hide()
		saveProfileButton.show()
		usernameElement.attr('readonly', false)
		
		statusElement.removeClass('error')
		statusElement.text('')
	})
	
	saveProfileButton.click(() => {
		saveProfileButton.hide()
		editProfileButton.show()
		usernameElement.attr('readonly', true)
		
		saveProfile({
			name: usernameElement.val()
		})
	})
})
