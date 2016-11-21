package urlshortener.team.web;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.hash.Hashing;

import urlshortener.common.domain.ShortURL;
import urlshortener.common.repository.ShortURLRepository;

@RestController
public class ShortNameController {
	
	@Autowired
	protected ShortURLRepository shortURLRepository;	
	
	
	@RequestMapping(value = "/idValidate", method = RequestMethod.GET)
	public ResponseEntity<String> shorName(@RequestParam("shortName") String shortName) {
	
		ShortURL l = shortURLRepository.findByKey(shortName);
		if (l == null) {			
			return new ResponseEntity<String>(shortName, HttpStatus.OK);

		} else {						
			System.out.println("shortName already exist");
			return new ResponseEntity<String>(shortName, HttpStatus.BAD_REQUEST);
		}	

	
	}
	
	@RequestMapping(value = "/idGenerate", method = RequestMethod.GET)
	public ResponseEntity<String> shorNameGenerate(@RequestParam("shortName") String shortName,
			@RequestParam("url") String url) {

		String id;
		ShortURL l = shortURLRepository.findByKey(shortName);
		String idR = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString();				

		if (l == null) {
			if (shortName.equals("")) {
				id = idR;
			}else{
				id = shortName;
			}
		} else {						
			System.out.println("shortName already exist");
			id = idR;
		}			
		return new ResponseEntity<String>(id, HttpStatus.OK);
	}	
	
}