$(function(){
	var account = JSON.parse(localStorage.getItem('account')),
	confirmedProducts = [],
	dateFormat = "mm/dd/yy"
	aidToIn = $('#aidToIn');
	valueIn = $('#valueIn');
	inputFields = $([]).add(aidToIn).add(valueIn)
	if (account === null) {
		location.href = 'login.html'
	}
	if (account.contracts.length > 1) {
		fillMyContracts(account.contracts)
		$('#searchBt').on('click',function() {
			searchOpt = {}
			searchOpt.aidFrom = $('#aidFromIn').val();
			searchOpt.aidTo = $('#aidToIn').val();
			searchOpt.pid = $('#pidIn').val()
			searchOpt.minDate = $('#minDateIn').datepicker('getDate');
			searchOpt.maxDate = $('#maxDateIn').datepicker('getDate');
			searchOpt.sort = $('#sortOldIn').is(":checked") ? "old" : "recent";
			for (key in searchOpt) {
				v = searchOpt[key];
				if (!v || v.length < 1 ) {
					delete searchOpt[key]
				} 
			}
			filteredContracts = filterMyContracts(searchOpt);
			fillMyContracts(filteredContracts);
		})
	}
	if (account.products.length == 0) {
		contractErr('You currently have no products to send')
		inputFields.prop('disabled',true);
	}
	else {
		setupNewContracts();
		fillMyProducts(account.products);
	}
    from = $("#minDateIn")
      .datepicker({
        defaultDate: "+1w",
        changeMonth: true,
        changeYear:true,
      })
      .on( "change", function() {
        to.datepicker( "option", "minDate", getDate( this ) );
      }),
    to = $( "#maxDateIn" ).datepicker({
      defaultDate: "+1w",
      changeMonth: true,
      changeYear:true,
    })
    .on( "change", function() {
      from.datepicker( "option", "maxDate", getDate( this ) );
    });

    function getDate( element ) {
    var date;
    		try {
    			date = $.datepicker.parseDate( dateFormat, element.value );
    		} catch( error ) {
    			date = null;
    		}
    		return date;
    }
    
	function fillMyContracts(ctr) {
		$('#myContracts tbody').empty();
		for (element in ctr) {		
			c = ctr[element];
			row = $('<tr></td>')
			row.append($('<td></td>').text(c.id))
			row.append($('<td></td>').text(getSentOrReceived(account.id,c.aidTo,c.aidFrom)))
			row.append($('<td></td>').text(c.value))
			row.append($('<td></td>').append($('<i></i>').addClass(getContractIconClasses(c))))
			$('#myContracts tbody').append(row)
			row.on('click',function(){
				location.href = 'contractView.html?cid=' + c.id;
			})
		}	
	$('#myContractsInfoTxt').empty();
	alert = $('<p></p>');
	alert.addClass('err')
	alert.text('Showing ' + ctr.length + ' of ' + account.contracts.length + ' contracts');
	$('#myContractsInfoTxt').append(alert);
	}
	
	function fillMyProducts(products) {	
		$('#myProducts').empty();
		for (prod in products) {
			p = products[prod]
			option = $('<option></option>')
			.val(p.id)
			.text(p.description + '  ' + p.RRP)
			$('#myProducts').append(option);
		}
	}
	
	function setupNewContracts() {
		chosenInfo = $('<p></p>').text('Products I want to include in this contract')
		$('#myChosenProducts').parent().prepend(chosenInfo)
		productInfo = $('<p></p>').text('All my products')
		$('#myProducts').parent().prepend(productInfo)
		
		$('#moveRightBt').on('click',function(){
			selected = $('#myProducts :selected');
			selected.remove();
			
			$('#myChosenProducts').append(selected)
			confirmedProducts = account.products.filter(function(p){
				return p.id == selected.val()
			})			
		})
		
		$('#moveLeftBt').on('click',function(){
			selected = $('#myChosenProducts :selected');
			selected.remove();
			console.log(selected.val())
			$('#myProducts').append(selected)
			confirmedProducts = confirmedProducts.filter(function(p){
				return p.id !== selected.val()
			})
			
			
		})
		
		$('#productDetailsBt').on('click',function(){
			pid = $('#myProducts :selected').val();
			if (pid !== undefined) {
				location.href = '/productView.html?pid=' + pid;
			}
		})
		
		$('#sendContractBt').on('click',function() {	 
			  to = $('#aidToIn').val().trim();
			  val = $('#valueIn').val().trim();
			  products = $('#myChosenProducts option')	  
			  if (products.length < 1) {
				  contractErr('Contracts must contain at least 1 product before you send them')
			  }
			  else if (!val || !validatePrice(val) ) {
				  contractErr('You must specify a value for your product in a correct price format e.g \'199.99\' Do not include currency symbols')
			  }  
			  else  {
				
				  $.post('../../CheckAid',{'aid':to },function(response){				 
					  if (!response.hasOwnProperty('success')) {
						 contractErr('The specified account number is not valid')
					  }
					  else {					 
						  confirmContract({'from':account.id,'to':to,'val':val,'products':confirmedProducts})
					   }
				  });
			 }			
		});
	}

	
	function getSentOrReceived(id,t,f) {
		return id == t ? "Received from: " + f : "Sent to: " + t 
	}
	
	function getContractIconClasses(c) {
		return c.confirmed == true ? "fa fa-fw fa-check" : "fa fa-fw fa-times" 
	}
	
	function sortByDate(o,n) {
		return new Date(o) < new Date(n);
	}
	
	
	function filterMyContracts(opt) {
		filteredContracts = account.contracts
		if (opt.hasOwnProperty('aidTo')) {
			filteredContracts = filteredContracts.filter(function(c) {
				return c.aidTo.includes(opt.aidTo);
			})
		}
		if (opt.hasOwnProperty('aidFrom')) {
			filteredContracts = filteredContracts.filter(function(c) {
				return c.aidFrom.includes(opt.aidFrom);
			})
		}
		if (opt.hasOwnProperty('pid')) {
			filteredContracts = filteredContracts.filter(function(c) {
				idList = getIdList(contract.products)
				return idList.some(function(el) {
					return el.includes(opt.pid)
				})
			})
		}
		if (opt.hasOwnProperty('maxDate')) {
			filteredContracts = filteredContracts.filter(function(c) {
				return sortByDate(c,opt.maxDate);
			})
		}
		if (opt.hasOwnProperty('minDate')) {
			filteredContracts = filteredContracts.filter(function(c) {
				return sortByDate(opt.minDate,c);
			})
		}
		if (opt.hasOwnProperty('sort')) {
			filteredContracts.sort(function(c0,c1) {
				if (opt.sort = "old") {
					return sortByDate(c1,c0);
				}
				else if (opt.sort = "recent") {
					return sortByDate(c0,c1);
				}
			})
		}
		return filteredContracts;
	}

	
	function contractErr(err) {
		box = $('#contractErrBox');
		box.empty();
		$('<p></p>').addClass('err').text(err).appendTo(box);
	}
	
	
	function getIdList(objArr) {
		idList = []
		for (i in objArr) {
			obj = objArr[i]
			if (obj.hasOwnProperty('id')) {
				idList.push(obj.id)			
			}
		}
		return idList;
	}
	
	 
	 function validatePrice(input) {
		 return  /^\d*(.\d{2})?$/.test(input);
	 }
	 
	 function confirmContract(opt) {
		 $('#modalErrBox').empty();
		 $('#submitContractBt').removeAttr('disabled')
		 $('#submitContractBt').on('click',function() {	   
			    opt.products = getIdList(opt.products).join();
				$.post('../../SubmitContract',opt,function(resp) {
					msg = $('<p></p>').text(resp.msg)
					if (resp.err) {
						msg.addClass('err')
					}
					else {
						msg.addClass('success')
					}
					msg.appendTo($('#modalErrBox'))
					$('#submitContractBt').attr('disabled',true)
				})	 
			  })
		 $.post('../../NewCidNumber',function (response) {
			 if (response.hasOwnProperty('cid')) {
				 $('#newCidTxt').text('CID: ' + response.cid);
				 opt['cid'] = response.cid;
				 
			 }
		 })	  
		 $('#fromTxt').text(account.id);
		 $('#toTxt').text(opt.to);
		 $('#valTxt').text( '£' + opt.val);
		 $('#dateTxt').text(new Date().toDateString())	
		 $('#confirmedProducts tbody').empty();
		 for (i = 0; i < opt.products.length; i++) {		 		
			 row = $('<tr></tr>')
			 row.append($('<td></td>').text(opt.products[i].id))
			 row.append($('<td></td>').text(opt.products[i].description))
			  row.append($('<td></td>').text(opt.products[i].RRP))
			 $('#confirmedProducts tbody').append(row);
		 }
		 $('#showModalBt').click(); 		
	 }	 	
})