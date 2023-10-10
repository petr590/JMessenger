/**
 * Вызывает fetch и назначает обработчики ошибок по умолчанию, которые выводят на экран сообщение об ошибке
 * @param {URL|string} url - адрес запроса
 * @param {Object} options - параметры запроса
 * @callback onSuccess(response) - функция, которая вызывается при успешном запросе. Можно не указывать
 * @callback onHttpError(response) - функция, которая вызывается при неуспешном ответе сервера. Если она вернёт true,
 *                                   то отработает стандартная обработка ошибки (alert с сообщением об ошибке). Можно не указывать
 * @callback onErrorCaught(error) - функция, которая вызывается при возникновении исключения. Если она вернёт true,
 *                                  то отработает стандартная обработка ошибки (alert с сообщением об ошибке). Можно не указывать
 * @return {Promise} Промис с результатом выполнения функции onSuccess
 */
export function fetchWithDefaultErrorHandlers(url, options, onSuccess, onHttpError, onErrorCaught) {
	return fetch(url, options)
			.then(async response => {
				if (response.ok) {
					if (typeof onSuccess === 'function') {
						return onSuccess(response)
					}
					
				} else {
					if (typeof onHttpError === 'function' && !onHttpError(response)) {
						return
					}
					
					let contentType = response.headers.get('Content-Type')
					
					if (contentType != null) {
						let types = contentType.split(';')
						
						if (types.includes('text/plain') || types.includes('application/json')) {
							let text = await response.text()
							
							alert(`Ошибка при выполнении HTTP запроса: ${response.status} ${response.statusText}${text == '' ? '' : ': '}${text}`)
							
							return
						}
					}
					
					alert(`Ошибка при выполнении HTTP запроса: ${response.status} ${response.statusText}`)
				}
				
			}).catch(error => {
				if (typeof onErrorCaught === 'function' && !onErrorCaught(error)) {
					return
				}
				
				console.log(error)
				console.trace()
				
				alert(`Ошибка: ${error.message}`)
			})
}


/**
 * Ищет элемент в элементе parent или создаёт новый элемент из переданного html и добавляет его в parent
 * @return Найденный или добавленный элемент
 */
export function $findOrAppendChild(parent, selector, html) {
	let element = parent.find(selector)
	if (element.length != 0) {
		return element
	}
	
	return $(html).append(parent)
}

