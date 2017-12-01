$(function(){
	var account = localStorage.getItem('account');
	if (account === null) {
		location.href = 'login.html'
	}
	else { 
		account = JSON.parse(account); 
	}
	$('#navNameTxt').text(account.name)
	var pid = getSearchParams('pid');
	if (pid) {
		$('#pidTxt').text(pid);
		$.post('../../GetProduct',{'pid': pid },function(response){
			if (response.hasOwnProperty('error')) {
				pageErr("Product details for '" + pid + "' could not be found");
			}
			else if (response.hasOwnProperty('product')) {
				var product = JSON.parse(response.product);
				fillProductInfo(product);
			}
		})
	}
	else pageErr("Product details not be found");
	
	function getSearchParams(k){
		 var p={};
		 location.search.replace(/[?&]+([^=&]+)=([^&]*)/gi,function(s,k,v){p[k]=v})
		 return k?p[k]:p;
	}
	
	function fillProductInfo(product) {
		fillContracts(product.contracts);
		$('#descriptionTxt').text(product.description)
		$('#ownerTxt').text(product.owner)
		$('#dateTxt').text(product.date)
		$('#sizeTxt').text(product.size)
		$('#rrpTxt').text(product.rrp)
	}
	
	function fillContracts(contracts) {
		$('#contractsTable tbody').empty();
		for (e in contracts) {
			c = contracts[e];
			row = $('<tr></tr>')
			let id = c.id;
			row.append($('<td></td>').text(c.id))
			row.append($('<td></td>').text(c.dc))
			row.append($('<td></td>').text(c.value))
			row.on('click',function(){
				location.href = 'contractView.html?cid=' + String(id);
			})
			$('#contractsTable tbody').append(row);
		}		
	}
	
	function pageErr(msg) {
		$('#modalTxt').text(msg)
		$('#modalBt').click();
	}
})

//==========================MAP================================


google.charts.load('current', {
        'packages':['geochart'],
        // Note: you will need to get a mapsApiKey for your project.
        // See: https://developers.google.com/chart/interactive/docs/basic_load_libs#load-settings
        'mapsApiKey': 'AIzaSyD-9tSrke72PouQMnMX-a7eZSW0jkFMBWY'
      });
      google.charts.setOnLoadCallback(drawRegionsMap);

      function drawRegionsMap() {
        var data = google.visualization.arrayToDataTable([
          ['Country', 'Popularity'],
          ['Germany', 200],
          ['United States', 300],
          ['Brazil', 400],
          ['Canada', 500],
          ['France', 600],
          ['RU', 700]
        ]);

        var options = {
                legend: 'none',
                hAxis: { minValue: 0, maxValue: 9 },
                curveType: 'function',
                pointSize: 20,
            };

        var chart = new google.visualization.GeoChart(document.getElementById('productMap'));

        chart.draw(data, options);
      }