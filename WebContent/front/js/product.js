$(function(){
	var account = JSON.parse(localStorage.getItem('account')),
	dateFormat = "mm/dd/yy";
	if (account == null) {
		location.href = 'login.html'
	}
	//account.type = "Manager"
	if (account.hasOwnProperty('type') && account['type'] != 'Manager' ) {
		$('#createProductModal').removeAttr('data-toggle'); 
		$('#modalTrigger').removeAttr('href');
		$('#modalTrigger').css('color','grey');
		$('#modalTrigger').css('cursor','not-allowed');
		pageErr('You are not authorized to create new products')
	}
	else {
		
		$('#modalTrigger').on('click',function(){
			var newPid = generateNewPid();
			$('#newPidTxt').text('PID: ' + newPid);
		})
		
		$('#submitBt').on('click',function(){
			size = $('#modalSizeIn').val().trim()
			description = $('#modalDescriptionIn').val().trim()
			rrp = $('#modalRrpIn').val().trim()
			if (!description || description.length < 10) {
				modalErr('Try to make your description as longer to inform potential buyers about the product');
			}
			else if (!rrp || !validatePrice(rrp)) {
				modalErr('Please enter a valid price for the product e.g \'77.99\' ')
			}
			else {
				data = { 'aid': account.id,
						 'pid': newPid,
						 'size': size,
						 'description': description,
						 'rrp': rrp}
				$.post('../../SubmitProduct',data, function response(resp) {
					msg = $('<p></p>').text(resp.msg)
					if (resp.err) {
						msg.addClass('err')
					}
					else {
						msg.addClass('success')
						newPid = generateNewPid();
					}
					msg.appendTo($('#modalErrBox'))
				})
			}
			
			
		})
	}
	if (account.products.length == 0) {
		productErr('You currently have no products to send')
	}
	else {
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
		fillMyProducts(account.products)
		$('#searchBt').on('click',function(){
			searchOpt = {}
			searchOpt.pid = $('#pidIn').val();
			searchOpt.RRP = $('#rrpIn').val();
			searchOpt.description = $('#descriptionIn').val();
			searchOpt.minDate = $('#minDateIn').datepicker('getDate');
			searchOpt.maxDate = $('#maxDateIn').datepicker('getDate');
			searchOpt.sort = $('#sortOldIn').is(":checked") ? "old" : "recent";
			for (key in searchOpt) {
				v = searchOpt[key];
				if (!v || v.length < 1 ) {
					delete searchOpt[key]
				} 
			}
			filteredProducts = filterMyProducts(account.products,searchOpt);
			fillMyProducts(filteredProducts);
		})
	}
	
	function fillMyProducts(products) {
		$('#myProducts tbody').empty();
		for (e in products) {		
			p = products[e];
			row = $('<tr></tr>')
			row.append($('<td></td>').text(p.id))
			row.append($('<td></td>').text(p.description))
			row.append($('<td></td>').text(p.rrp))
			$('#myContracts tbody').append(row)
			row.on('click',function(){
				location.href = 'productView?pid=' + p.id;
			})
			$('#myProducts tbody').append(row);
		}		
	productErr('Showing ' + products.length + ' of ' + account.products.length + ' products')
	}
	
	function productErr(msg) {
		$('#myProductsInfoTxt').empty();
		err = $('<p></p>');
		err.addClass('err')
		err.text(msg);
		$('#myProductsInfoTxt').append(err);
	}
	
	function pageErr(msg) {
		$('#pageErrBox').empty();
		err = $('<p></p>');
		//err.addClass('err')
		err.text(msg);
		$('#pageErrBox').append(err);
	}
	
	function filterMyProducts(products,opt) {
		filteredProducts = products;
		if (opt.hasOwnProperty('pid')) {
			filteredProducts = products.filter(function(p) {
				return p.id.includes(opt.pid);
			})
		}
		if (opt.hasOwnProperty('RRP')) {
			filteredProducts = filteredProducts.filter(function(p) {
				return p.rrp.includes(opt.RRP);
			})
		}
		if (opt.hasOwnProperty('description')) {
			filteredProducts = filteredProducts.filter(function(p) {
				return p.description.includes(opt.description);
			})
		}
		if (opt.hasOwnProperty('maxDate')) {
			filteredProducts = filteredProducts.filter(function(p) {
				return sortByDate(p.date,opt.maxDate);
			})
		}
		if (opt.hasOwnProperty('minDate')) {
			filteredProducts = filteredProducts.filter(function(p) {
				return sortByDate(opt.minDate,p.date);
			})
		}
		if (opt.hasOwnProperty('sort')) {
			filteredProducts.sort(function(p0,p1) {
				if (opt.sort = "old") {
					return sortByDate(p1,p0);
				}
				else if (opt.sort = "recent") {
					return sortByDate(p0,p1);
				}
			})
		}
		return filteredProducts;
	}
	
	function sortByDate(o,n) {
		return new Date(o) < new Date(n);
	}
	
	function validatePrice(input) {
		return  /^\d*(.\d{2})?$/.test(input);
	}
	
	function modalErr(err) {
		box = $('#modalErrBox');
		box.empty();
		$('<p></p>').addClass('err').text(err).appendTo(box);
	}
	
	function generateNewPid() {
		$.post('../../NewPidNumber',function (response) {
			 if (response.hasOwnProperty('pid')) {
				 $('#newPidTxt').text('PID: ' + response.pid);
				 return response.pid 
			 }
		 })	
	}
});