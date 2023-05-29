package demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PayloadController {

	@Autowired
	private PayloadDispatcher payloadDispatcher;
	
	@PostMapping(value = "/payloads",
			consumes = {MediaType.APPLICATION_JSON_VALUE},
			produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
    public boolean sendMessage(@RequestBody Payload payload) {
        return payloadDispatcher.dispatch(payload);
    }

}
