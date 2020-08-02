
$(function(){
	
	  var i = 0;

	  if($("div").is(".slider")){
	  
		  var slider_message=[
			  'быстрый и удобный доступ',
			  'создавай свои папки',
			  'храни любые файлы',
			  'делись ими с друзьями',
			  'Cloud - новый удобный сервис для хранения данных'];		  
	  
		  setInterval(function () {
			  
			  $('.slider').animate({opacity:0},3000,()=>{
				  $('.slider').text(slider_message[i]).animate({opacity:1},1000)
				  
			  });
		  
			  (i<slider_message.length-1) ? i++ : i = 0;

		  }, 4000);			  
	  
		  
	  }else{
		  show_folder_content($('div[folderid]').attr('folderid'));
	  }

	  
	  $(".cloud").click(function() {

		  show_folder_content();
		  
		  $(".edit_cloud").toggleClass('nav-item edit_cloud').toggleClass('nav-item hidden edit_cloud');
		  
	  });
	  
	  $(".users").click(function() {

		  get_users();
		  
		  $(".edit_users").toggleClass('nav-item edit_users').toggleClass('nav-item hidden edit_users');
		  
	  });
	  
	  $(".info").click(function() {
	  
		  if(!$(".edit_cloud").hasClass("hidden")){
			  $(".edit_cloud").toggleClass('nav-item hidden edit_users');
		  }
		  
		  if(!$(".edit_users").hasClass("hidden")){
			  $(".edit_users").toggleClass('nav-item hidden edit_users');
		  }
		  
	  });
	
})

var delay_timeout = 0;



function show_folder_content(folder_id) {
	
	$.ajax({   
		type: "POST",
		url: "/getfiles",
		data: {folderId: folder_id},
		dataType: "json",
		beforeSend: show_loader,			
		success: function (data) {
			
			var content='';
			
			console.log(data.parentFolderId);
			
			if(data.parentFolderId!=null){
				content +=`
		    		<div class="prev mb-3" parentid="${data.parentFolderId}">  			
						<a href="#" onclick="show_folder_content(${data.parentFolderId})">
							<i class="fas fa-chevron-circle-left fa-3x"></i>
						</a>
					</div>`;
				
			}
			
			
			if(data.files.length>0){

				content += `
				<div class="root-wrapper" folderid="${data.folderId}">
			    	<table class="table">
				    	<thead class="thead-light">
				    		<tr>
				    			<th scope="col">Название</th>
				    			<th scope="col">Размер</th>
				    			<th scope="col">Дата создания</th>
				    			<th scope="col"></th>
				    			<th scope="col"></th>
				    			<th scope="col"></th>
				    		</tr>
				    	</thead>
				    	<tbody>`;
				
				var icon;
				var del_icon = "<div></div>";
				var share_icon = "<div></div>";
				var download_icon = "<div></div>";
				
				data.files.forEach(function(item) {
					
			    	if(item.type==null){
			    		
			    		icon = `
			    		<a class="folder" href="#" onclick="show_folder_content(${item.id})">
			    			<i class='fas fa-folder fa-3x'></i>
			    			<span>${item.name}</span>
			    		</a>`;
			    		
			    	}else{
			    		icon =`
			    		<div class="file">  			
							<div class="d-inline-block">
							 	<div class="file-icon">
							 		<div><i class="fas fa-file fa-3x"></i></div>
							 		<div class="file-icon-type"><small>${item.type}</small></div>
							 	</div> 	
							</div>
							<span>${item.name}</span>
						</div>`;
			    		
			    		download_icon = `<a href="#" onclick="download_file(${item.id})"><i class="fas fa-file-download"></i></a>`;
			    		share_icon = `<a href="#" onclick="share_file(${item.id})"><i class="fas fa-share-alt"></i></a>`;
			    		del_icon = `<a href="#" onclick="delete_file(${item.id})"><i class="fas fa-trash-alt"></i></a>`;
			    	}
					
			    	content+=`
					    <tr>
							<td>
								${icon}
								
							</td>
							<td>${(item.size==null) ? '' : item.size}</td>
							<td>${item.date}</td>
							<td>${download_icon}</td>
							<td>${share_icon}</td>
							<td>${del_icon}</td>
						</tr>`; 		
					
				});
				
				content+=`
				</tbody>
				</table>
				</div>`;
			}else{
				content+=`
					<div class="root-wrapper" folderid="${data.folderId}">
						<div>Здесь пока что ничего нет</div>
					</div>`;				
			}
			
			remove_loader(()=>$('.content-wrapper').html(content));
			
		},
		error: function (data){
			content=`
				<div class="root-wrapper" folderid="${folderId}">
					<div>Сервис недоступен</div>
				</div>`;
			remove_loader(()=>$('.content-wrapper').html(content));
		}
	});
	
}


function add_file() {

	var content = `
		<div class='popup-wrapper'>
			<div class='activity popup-inner'>
				<form action='/home' method='GET' enctype='multipart/form-data'>
					<input type='file' id="file_v" class='form-control-file mb-3' name='image' id='exampleFormControlFile'>
				</form>
				<div class='d-flex justify-content-end'>
					<button class='btn btn-primary' onclick="upload_file()">Загрузить</button>
				</div>
			</div>
		</div>`;
	
		
	$('body').prepend(content);	
	
	$(".popup-wrapper").bind('click',function(e){
		
		if(e.target.className=='popup-wrapper'){
			$(".popup-wrapper").remove();
		};
	  
		
	})
}


function upload_file(){
	
	var formData = new FormData();
	var folderId = $('div[folderid]').attr('folderid');
	

	jQuery.each($('#file_v')[0].files, function(i, file) {
		formData.append('file', file);
	});
	
	formData.append('folderId', folderId);


	$.ajax({
		url: "/upload/file",
		type: "POST",
		dataType : "json", 
		cache: false,
		contentType: false,
		processData: false,			
		data: formData, //указываем что отправляем
		success: function(data){
        	$(".popup-wrapper").remove();
        	show_folder_content(folderId);
		}
	});
	
	
}

function add_folder() {
	
	var folderId = $('div[folderid]').attr('folderid');
	var userId = $('div[userId]').attr('userId');
	var content = `
		<div class='popup-wrapper'>
			<div class='activity popup-inner'>
				<form action='/home' method='GET' id='add-folder'>
					<input type='text' class='form-control mb-2' name='folderName' placeholder="Введите название" required>
					<input type='hidden' name='parentFolderId' value="${folderId}">
					<input type='hidden' name='userId' value="${userId}">
				</form
				<div class='d-flex justify-content-end'>
					<button class='btn btn-primary' onclick="upload_folder()">Добавить</button>
				</div>
			</div>
		</div>`;
		
	$('body').prepend(content);
	
	$(".popup-wrapper").bind('click',function(e){
		  
		if(e.target.className=='popup-wrapper'){
			$(".popup-wrapper").remove();
		};
	})
			
}

function download_file(file_id) {
	
	$.ajax({   
		type: "POST",
		url: "/download/",
		data: {fileId : file_id},
		success: function (data, textStatus, xhr) {
			
			console.log(xhr.getResponseHeader('Content-Disposition'));
			var binaryData = [];
			binaryData.push(data);
			
			const url = window.URL.createObjectURL(new Blob(binaryData, {type: xhr.getResponseHeader('Content-Type')}));
			
			const dummy = document.createElement('a');
			dummy.href = url;
			dummy.download = xhr.getResponseHeader('Content-Disposition');

			document.body.appendChild(dummy);
			dummy.click();

		}
	});
	
}

function upload_folder() {
	
	var formData = $("#add-folder").serializeArray();
	var data = {};
	
	$(formData).each(function(index, obj){
	    data[obj.name] = obj.value;
	});

    $.ajax({
		type: "POST",
		contentType : 'application/json',
		url: "/upload/folder",
        data: JSON.stringify(data),
        success: function(response) {
        	
        	$(".popup-wrapper").remove();
        	show_folder_content(data.parentFolderId);
        	
    	},
    	error: function(response) {
    		$(".popup-wrapper").remove();
    		//<div>Не удалось загрузить</div>
    	}
 	});		
	
}


function add_user() {

	var content = `
		<div class='popup-wrapper'>
			<div class='activity popup-inner'>
				<div class='d-flex justify-content-center mb-3'>
					<h3>Добавление нового пользователя</h3>
				</div>	
				<form id='add-user' action=''>
					<input type='text' class='form-control mb-2' name='name' placeholder="Имя" required>
					<input type='text' class='form-control mb-2' name='login' placeholder="Логин" required>
					<input type='password' class='form-control mb-2' name='password' placeholder="Пароль" required>
					<input type='text' class='form-control mb-2' name='email' placeholder="Email" required>
					<select class="form-control mb-2" name="role_id" required>
					  <option value="" disabled selected>Укажите роль пользователя</option>
					  <option value="1">Admin</option>
					  <option value="2">User</option>
					</select>
					<select class="form-control mb-3" name="tariff_id" required>
						<option value="" disabled selected>Выберите тариф</option>
						<option value = "1">Базовый</option>
						<option value = "2">Средний</option>
						<option value = "3">Профессионал</option>
					</select>
				</form>
				<div class='d-flex justify-content-end' >
					<button type='button' class='btn btn-primary' onclick='send_user_form()'>Добавить</button>
				</div>
			</div>
		</div>`;
		
	$('body').prepend(content);

	$(".popup-wrapper").bind('click',function(e){
		  
		if(e.target.className=='popup-wrapper'){
			$(".popup-wrapper").remove();
		};
	})			
}

function send_user_form(){

	var formdata = $("#add-user").serializeArray();
	var data = {};
	$(formdata).each(function(index, obj){
	    data[obj.name] = obj.value;
	});

    $.ajax({
		type: "POST",
		contentType : 'application/json',
		url: "/user",
        data: JSON.stringify(data),
        success: function(response) {
        	$(".popup-wrapper").remove();
        	get_users();
    	}
    /*
    	error: function(response) {
    		$(".popup-wrapper").remove();
    	}*/
 	});	
	
}



function get_users() {
	
	$.ajax({   
		type: "POST",
		url: "/users",
		dataType: "json",
		beforeSend: show_loader,
		success: function (users) {
			
			var content = `
			<div class="root-wrapper">
			<table class="table">
				<thead class="thead-light">
					<tr>
						<th scope="col">Имя</th>
						<th scope="col">Логин</th>
						<th scope="col">Роль</th>
						<th scope="col">Email</th>
						<th scope="col">Тариф</th>
						<th scope="col"></th>
						<th scope="col"></th>
					</tr>
				</thead>
			<tbody>`;
			
			users.forEach(function(user) {
				content +=`
				<tr>
					<td>${user.name}</td>
					<td>${user.login}</td>
					<td>${user.role}</td>
					<td>${user.email}</td>
					<td>${user.tariff}</td>
					<td><a class="edit-user" href="#" onclick="edit_user(${user.id})"><i class="fas fa-pen"></i></a></td>
					<td><a class="delete-user" href="#" onclick="delete_user(${user.id})"><i class="fas fa-trash-alt"></i></a></td>
				</tr>`;
				
			});
			
			content +=`
			</tbody>
			</table>
			</div>`;
				
			remove_loader(()=>$('.content-wrapper').html(content));
		}
	});	
	
}

function delete_user(user_id) {
	
	$.ajax({   
		type: "DELETE",
		url: "/user",
		data: {id: user_id},
		success: function (users) {
			
			var content = "<div>Пользователь успешно удален</div>";
				
			$('.content-wrapper').html(content);
			get_users();
		}
	});
	
}

function edit_user(user_id) {
	
	var content;
	$.ajax({   
		type: "GET",
		url: `/user/${user_id}`,
		dataType: "json",
		success: function (user) {

			content = `
				<div class='popup-wrapper'>
					<div class='activity popup-inner'>
						<div class='d-flex justify-content-center mb-3'>
							<h3>Редактирование пользователя</h3>
						</div>	
						<form id='edit-user' action='' method='POST'>
							<input type='hidden' class='form-control mb-2' name='id' value="${user_id}">
							<input type='text' class='form-control mb-2' name='name' value="${user.name}" required>
							<input type='text' class='form-control mb-2' name='login' value="${user.login}">
								<input type='text' class='form-control mb-2' name='email' value="${user.email}">
							<select class="form-control mb-2" name="role_id" required>
							  <option value="" disabled selected>Укажите роль пользователя</option>
							  <option value="1">Admin</option>
							  <option value="2">User</option>
							</select>
							<select class="form-control mb-3" name="tariff_id" required>
								<option value="" disabled selected>Выберите тариф</option>
								<option value = "1">Базовый</option>
								<option value = "2">Средний</option>
								<option value = "3">Профессионал</option>
							</select>
						</form>
						<div class='d-flex justify-content-end'>
							<button type='button' class='btn btn-primary' onclick='update_user()'>Сохранить</button>
						</div>
					</div>
				</div>`;
			
			$('body').prepend(content);
			
			$(".popup-wrapper").bind('click',function(e){
				  
				if(e.target.className=='popup-wrapper'){
					$(".popup-wrapper").remove();
				};
			})		

		}
		
	});
	
}


function update_user() {
	
	var formdata = $("#edit-user").serializeArray();
	var data = {};
	$(formdata).each(function(index, obj){
	    data[obj.name] = obj.value;
	});

    $.ajax({
		type: "POST",
		contentType : 'application/json',
		url: "/user/edit",
        data: JSON.stringify(data),
        success: function(response) {
        	
        	$(".popup-wrapper").remove();
        	get_users();
    	
    	},
    	error: function(response) {
    		$(".popup-wrapper").remove();
    	}
 	});	
	
}

function share_file(id) {
	
	var content;
	$.ajax({   
		type: "GET",
		url: `/share/${id}`,
		dataType: "json",
		beforeSend: function(){
        	$('main').append('<img id="loader" src="/icons/loader.gif">');
    	},			
		success: function (hlink) {

			content = `
				<div class='popup-wrapper'>
					<div class='activity popup-inner'>
						<h5>Ссылка на файл</h5>
						<form id='edit-user' action='' method='POST'>
							<input type='text' class='form-control mb-2' name='name' value="${hlink}" required>
						</form>
					</div>
				</div>`;
			
			$('main').html(content);
		}
		
	});
	
}

function show_tariff(user_id) {
	
    $.ajax({
		type: "POST",
		dataType: "json",
		url: `/user/info/${user_id}`,
		beforeSend: show_loader,
        success: function(data) {
        	
			var content = `
	        	<div class="tariff">
		        	<div class="d-flex justify-content-end align-items-center">
		        		<div>
		        			<h2>${data.tariffName}</h2>
		        		</div>
		        		<div>
		        			<div id="progressbar"></div>
		        		</div>
		        	</div>
		        	<div class="col mb-3 tariff-header">
		        		<h2>Доступные тарифы</h2>
		        	</div>`;
			
				var count = 0;
		
				
				data.offers.forEach(function(offer) {
					
					console.log(count);
			
					
					if(count==0) {
						content += '<div class="row mb-3">';
						
					}
					
					content +=`

						<div class="col-sm-4">	    
							<div class="card">
								<div class="card-body">
									<div class="tariff-name mb-5">${offer.name}</div>
									<div class='d-flex justify-content-end' >
										<button class='btn btn-primary' onclick='change-tariff(${offer.id})'>Выбрать</button>
									</div>
								</div>
							</div>	
						</div>`;

					if(count==2){
						content += '</div>';
						count = 0;
					}else{
						count++;

					}
					
					
				});
				
				content +='</div>';
					
				remove_loader(
						function(){
							
							$('.content-wrapper').html(content);
							create_progress_bar(data.usedSize);
						}
				);
				

    	
    	},
    	error: function(response) {
    		$(".popup-wrapper").remove();
    	}
 	});	
	
}


function show_loader(){
	
	delay_timeout = Date.now();
	$('main').append('<div id="loader"></div>')
	$('#loader').animate({opacity:1},1000);
	
}

function remove_loader(show_content){
	
	var delta = Date.now() - delay_timeout;
	
	if(delta>1000){
		delta = 0;
	}else{
		delta = 1000 - delta;
	}
	setTimeout(()=>{
		show_content();
		$('[id=loader]').animate({opacity:0},1000,()=>{
			$('[id=loader]').remove();		
		});
	
	},delta);
	
}


function create_progress_bar(size){
	
	var bar = new ProgressBar.Circle('#progressbar', {
		  color: "#6f42c1",
		  strokeWidth: 40,
		  trailWidth: 8,
		  easing: 'easeInOut',
		  duration: 1400,
		  text: {
		    autoStyleContainer: false
		  },
		  from: { color: "#20c997", width: 2 },
		  to: { color: '#dc3545', width: 10 },
		  // Set default step function for all animate calls
		  step: function(state, circle) {
		    circle.path.setAttribute('stroke', state.color);
		    circle.path.setAttribute('stroke-width', state.width);
		
		    var value = Math.round(circle.value() * 100);
		    
		    console.log(circle.value());
		    
		    if (value === 0) {
		      circle.setText('');
		    } else {
		      circle.setText(value);
		    }
		
		  }
		});
		bar.text.style.fontFamily = '"Raleway", Helvetica, sans-serif';
		bar.text.style.fontSize = '2rem';
		
		bar.animate(size);  // Number from 0.0 to 1.0
	
}





