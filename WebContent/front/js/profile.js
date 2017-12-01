$(function(){
	var account = localStorage.getItem('account'),
	oldPw = $('#oldPwIn'),
	pwIn = $('#pwIn'),
	pwInRepeat = $('#pwInRepeat'),
	phoneIn = $('#phoneIn'),
	emailIn = $('#emailIn'),
	addressIn = $('#addressIn'),
	houseIn = $('#houseIn'),
	cityIn = $('#cityIn'),
	countryIn = $('#countryIn'),
	zipIn = $('#zipIn'),
	userFields = $( [ ] ).add(phoneIn).add(emailIn).add(addressIn),
	formFields = $( [ ] ).add(oldPw).add(pwIn).add(pwInRepeat),
	allFields = $.merge(userFields,formFields);
	pwForm = $("form");
	warned = false,
	emailRegex = /^[a-zA-Z0-9.!#$%&'*+\/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/,
	phoneRegex = /^\+?[0-9. ()-]{10,25}$/,
	account = JSON.parse(account);
	if (account === null) {
		location.href = 'login.html'
	}
	$('#navNameTxt').text(account.name)
	fillProfileInfo(account)
	fillAddressInfo(account.address)
	$('#addressInfo').css('display','none')

	function fillProfileInfo(account) {
		$('#nameTxt').text(account.name)
		$('#unameTxt').text(account.username)
		$('#dobTxt').text('Date of birth: ' + account.dob)
		$('#phoneTxt').text(account.phone)
		$('#emailTxt').text(account.email)
		$('#aidTxt').text(account.id)
	}
	
	function fillAddressInfo(address) {
		if (typeof(address) == 'object') {
			$('#streetTxt').text(address.street) 
			$('#cityTxt').text(address.city)
			$('#countyTxt').text(address.county)
			$('#postcodeTxt').text(address.postcode)		
		}
		else {
			$('#streetTxt').text(address)
		}
	}
	
	$('#warnBt').on('click',function() {
		if($('#checkbox').is(':checked')) {
			$('#warnControls').slideUp(600,function() {
				$('#warnControls').empty();
				$('#warnControls').slideDown(600,function() {
					$('<i></i>').addClass('fa fa-fw fa-check-circle-o fa-3x')
								.appendTo($('#warnControls'));
				allFields.removeAttr('disabled');
				warned = true;					
				})
			});							
		}
	})
	
	$('#pwBt').on('click',function() {
		pw = pwIn.val();
		pw1 = pwInRepeat.val()
		pwChange(pw,pw1);
	});
	
	function pwChange(pw,pw1) {		
		if(!warned) { return }
		else if (pw !== pw1) {
			pwErr('passwords do not match')
		}
		else if (pw < 7 || pw.length > 15 ) {
			pwErr('password must be between 7 and 15 characters')			
		}
		else {
			$('#pwErrBox').empty();
			$.post('../../ChangeUserDetails',{"aid":account.id,"pw":pw},function(response) {
				showModal(response.msg)	
			});
		}		
	};
	
	$('#emailBt').on('click',function(){
		email = emailIn.val()
		emailChange(email);
	})
	
	function emailChange(email) {
		if(!warned) { return }
		else if (!emailRegex.test(email)) {
			pageErr('Email address does not match the required format, please try again')	
		}
		else {
			$.post('../../ChangeUserDetails',{"aid":account.id,"email":email},function(response) {
				$('#pageErrBox').empty();
				showModal(response.msg)
				if (response.success) {
					$('#emailTxt').text(email)
					account.email = email;
				}											
			});
		}
	}
	
	$('#phoneBt').on('click',function(){
		phone = $('#phoneIn').val()
		phoneChange(phone);
	})
	
	function phoneChange(phone) {	
		if(!warned) { return }
		else if (!phoneRegex.test(phone)) {
			pageErr('Phone number does not match the required format, please try again');
		}
		else {
			$.post('../../ChangeUserDetails',{"aid":account.id,"phone":phone},function(response) {
				$('#pageErrBox').empty();
				showModal(response.msg)	
				if (response.success) {
					account.phone = phone	
					$('#phoneTxt').text(phone)
				}														
			});
		}
	}
	
	$('#addressBt').on('click',function(){
		address = getAddressInfo();
		addressChange(address);
	})
	
	function addressChange(address) {
		console.log(address);
		if(!warned) { return }
		else if (address.malformed) {
			pageErr('A full new address has not been specified. If you wish to change your current address, please enter anew one. ' + 
					'You can confirm the address has been entered correctly by looking at the required ' +
					'fields, when you click "Input manually" button')
			}
		else {
			$.post('../../ChangeUserDetails',{"aid":account.id,"address":address},function(response) {
				$('#pageErrBox').empty();
				showModal(response.msg)
				if (response.success) {
				account.address = address
				fillAdressInfo(response.address)		
			}
		})		
	  }	
	}
	$('#manualBt').on('click',function(){
		if(!warned) { return }
		$('#addressInfo').slideDown(600,function(){
			$('#addressInfo').css('border-bottom','1px solid black')
		});
	
	})
	
	
	//TODO
	function downloadKeyPair() {
		
	}
	
	function getAddressInfo() {
		address = {}
		address.house = houseIn.val();
		address.city = cityIn.val();
		address.country = countryIn.val();
		address.zip = zipIn.val();
		for (value in Object.values(address)) {
			if (value.length < 2) {
				address.malformed = true
			}
		}
		return address
	}
	
	function showModal(msg) {
		$('#modalTxt').text(msg);
		$('#modalBt').click();		
	}
	
	function pwErr(msg) {	
		$('<span></span>').text(msg)
						  .addClass('err')
						  .css('padding-left','20px')
						  .appendTo($('#pwErrBox'));
	}
	
	function pageErr(msg) {	
		$('#pageErrBox').empty();
		$('<span></span>').text(msg)
						  .addClass('err')
						  .appendTo($('#pageErrBox'));
	}
	
});