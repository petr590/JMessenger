package x590.jmessenger.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ResourceController {

	@GetMapping(value = "/style/{file}", produces = "text/css")
	public String style(@PathVariable String file) {
		return "style/" + file;
	}

	@GetMapping(value = "/script/{file}", produces = {"application/javascript", "text/x-jquery-tmpl"})
	public String script(@PathVariable String file) {
		return "script/" + file;
	}


	/**
	 * Небольшой костыль для jquery - нам не надо, чтобы thymeleaf парсил скрипты jquery,
	 * поэтому придётся менять расширение файла на "rjs" (raw js).
	 * Можно добавить сюда другие шаблоны файлов, которые не должны обрабатываться thymeleaf-ом
	 */
	@GetMapping(value = "/script/jquery-{file}.js", produces = {"application/javascript", "text/x-jquery-tmpl"})
	public String rawScript(@PathVariable String file) {
		return "script/jquery-" + file + ".rjs";
	}
}
