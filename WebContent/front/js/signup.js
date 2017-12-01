$(function(){
	$('#dobinput').datepicker({
		changeMonth: true,
	    changeYear: true,
	});
	
	$('#newbt').on('click',function(){
		formErr('clear');
		var formData = {}
		$('#signupform *').filter(':input').each(function(i,e){
			name = $(e).attr('name')
		   formData[name] = $(e).val();
		});
		if (validate(formData)) {
			$.post('NewAccount',formData,function(response) {
				if (response.account) {
					sessionStorage.sessionAccount = response.account
					location.href = "/first/accountHome.html"
				}
				else if (response.error) {
					formErr(response.error)
				}
			})
		 }				
	});
	
	function validate(data) {
		valid = true;
		validateMap = {'firstName':vn,
					   'secondName':vn,
					   'dob':vn,
					   'email':vemail,
					   'uname':vuname,
					   'pw':vpw,
					   'phone':vphone,
					   'address': vaddress }
		
		for (name in data) {
			valid = valid && validateMap[name](data[name])
			if (!valid) {
				formErr(name);
				break;
			}
		}
		return valid;
	}
	
	var vn = function(str){
		return (str && str.length > 1);			
	}
	
	var vemail = function(email){
		regex = /^[a-zA-Z0-9.!#$%&'*+\/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/
		return (regex.test(email));			
	}
	
	var vuname = function(uname){
		return (uname && uname.length > 7);			
	}
	
	//could change this function if you want to be more strict about what password are 'valid'
	//but you would have to change the 'formErr' function as well to give the correct error message
	var vpw = function(pw){
		return (pw && pw.length > 7 && /\d/.test(pw));			
	}
	
	var vphone = function(number){
		regex = /^\+?[0-9. ()-]{10,25}$/
		return (regex.test(number));			
	}
	
	//TO DO!!!!
	var vaddress = function(adr){
		return (adr && adr.length > 1);	
	}
	
	function formErr(err){
		e = $('#errorBox');
		switch(err) {
	    case "firstName":
	    e.text('The first name field is required')
	        break;
	    case "secondName":
	    		e.text('The second name field is required')
	        break;
	    case "dob":
	    e.text('Please enter your date of birth in the format dd/mm/yyyy');
		    break;
	    case "email":
		    e.text('Email is required in the format abc@xyz.com');
			break;    
	    case "uname":
	    		e.text('Username must be at least 7 charachters long');
	    		break;
	    case "uname_taken":
    		e.text('This username is already in use, usernames must be unique');
    			break;	
	    case "pw":
	    		e.text('Password must be at least 7 charachters long and contain at least one number');
	    		break;
	    case "phone":
	    		e.text('Phone number is required and must be at least 10 digits long');
	    		break;
	    case "address":
    		e.text('Adress is required');
    			break;
	    case "phone":
    		e.text('Phone number is required and must be at least 10 digits long');
    			break;
	    case "internal":
    		e.text('Account could not be created due to an internal error, please try again');
    			break;	
	    case "clear":
    			e.text('');
    			break;
	    	default:
	    		e.text(err);
	        break;
	}
 }
	
});
