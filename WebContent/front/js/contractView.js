$(function(){
	var account = localStorage.getItem('account');
	if (account === null) {
		location.href = 'login.html'
	}
	else { 
		account = JSON.parse(account); 
	}
	$('#navNameTxt').text(account.name)
	var cid = getSearchParams('cid');
	if (cid) {
		$('#cidTxt').text(cid);
		$.post('../../GetContract',{'cid': cid },function(response){
			if (response.hasOwnProperty('error')) {
				pageErr("Contract details for '" + cid + "' could not be found");
			}
			else if (response.hasOwnProperty('contract')) {
				var contract = JSON.parse(response.contract);
				fillContractInfo(contract);
			}
		})
	}
	else {
		pageErr("Contract details not be found");
	}
	
	function getSearchParams(k){
		 var p={};
		 location.search.replace(/[?&]+([^=&]+)=([^&]*)/gi,function(s,k,v){p[k]=v})
		 return k?p[k]:p;
	}
	
	function fillContractInfo(contract) {
		fillProducts(contract.products);
		$('#fromTxt').text(contract.aidFrom)
		$('#toTxt').text(contract.aidTo)
		$('#dcTxt').text(contract.date)
		$('#valueTxt').text(contract.value)
		var icon = $('<i></i>').addClass('fa fa-fw fa-3x')
		$('#contractIcon').append(icon);
	if (contract.confirmed == true) {
		icon.addClass('green fa-check-circle')
		icon.css('color','#191');
		$('#contractIconBt').remove();
	}
	else {
		icon.addClass('red fa-times-circle')
		icon.css('color','#911');
		$('#contractIcon').on('click',function(){
			$('#contractIconBt').remove();
			$('#contractConfirmForm').slideDown();
			$('#contractConfirmFormBt').on('click', confirmBtListener )
			})
		}
	}
	
	function confirmBtListener() {
		var pw = $('#pwIn').val();
		var pwTwo = $('#pwInTwo').val();
		if (pw !== pwTwo) pwFormErr('Passwords do not match');
		else if (pw.length < 7) pwFormErr('Password is incorrect');
		else {
			$.post('../../ConfirmContract',{'cid': cid,'pw':pw},function(response) {
				$('pwFormErrBox').empty();
				if (!response.hasOwnProperty('error')) {
					$('#modalTxt').text(response.msg)
					contract.confirmed == true;
					fillContractInfo(contract);
				}
				else $('#modalTxt').text(response.error);				
				$('#modalBt').click();
			})
		}
	}
	
	function fillProducts(products) {
		$('#products tbody').empty();
		for (e in products) {
			p = products[e];
			row = $('<tr></tr>')
			let id = p.id;
			row.append($('<td></td>').text(p.id))
			row.append($('<td></td>').text(p.description))
			row.append($('<td></td>').text(p.rrp))
			row.on('click',function(){
				location.href = 'productView.html?pid=' + String(id);
			})
			$('#products tbody').append(row);
		}		
	}
	
	function pageErr(msg) {
		$('#modalTxt').text(msg)
		$('#modalBt').click();
	}
	
	function pwFormErr(msg) {
		$('#pwFormErrBox').empty()
		var err = $('<p></p>').text(msg).addClass('err');
		$('#pwFormErrBox').append(err);
	}
})