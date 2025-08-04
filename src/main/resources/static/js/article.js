const deleteButton = document.getElementById('delete-btn');
if(deleteButton){
    deleteButton.addEventListener("click", event=>{
        let id = document.getElementById('article-id').value;
        function success(){
            alert('삭제 완료되었습니다.');
            location.replace(`/articles`);
        }
        function fail(){
            alert('삭제 실패했습니다.');
            location.replace(`/articles/`+id);
        }
        httpRequest("DELETE",`/api/articles/${id}`, null, success, fail);
    })
}
const modifyButton = document.getElementById('modify-btn');
if(modifyButton){
    modifyButton.addEventListener("click", event=>{
        let params = new URLSearchParams(location.search);
        let id = params.get('id');

        let body = JSON.stringify({
            title:document.getElementById("title").value,
            content:document.getElementById("content").value
        });
        function success(){
            alert('수정 완료되었습니다.');
            location.replace(`/articles/`+id);
        }
        function fail(){
            alert('수정 실패했습니다.');
            location.replace(`/articles/`+id);
        }
        httpRequest("PUT",`/api/articles/${id}`, body, success, fail);
    })
}

const createButton = document.getElementById('create-btn');
if(createButton){
    createButton.addEventListener("click", event=>{
        let body = JSON.stringify({
            title:document.getElementById("title").value,
            content:document.getElementById("content").value
        });
        function success(){
            alert('등록 완료되었습니다.');
            location.replace(`/articles`);
        }
        function fail(){
            alert('등록 실패했습니다.');
            location.replace(`/articles`);
        }
        httpRequest("POST","/api/articles", body, success, fail);
    })
}
function getCookie(key){
    let result = null;
    let cookie = document.cookie.split(";");
    cookie.some(function (item){
        item = item.replace(" ", "");
        let dic = item.split("=");
        if(key === dic[0]){
            result = dic[1];
            return true;
        }
    });
    return result;
}

function httpRequest(method, url, body, success, fail){
    fetch(url, {
        method: method,
        headers:{
            Authorization : "Bearer " + localStorage.getItem("access_token"),
            "Content-Type" : "application/json",
        },
        body : body,
    }).then((response) =>{
        // 성공 했을시(엑세스 토큰이 정상일 경우)
        if(response.status === 200 || response.status === 201 ){
            return success();
        }
        // 실패 했을시
        const refresh_token = getCookie("refresh_token");
        // 통신 코드가 401번인 경우 RefreshToken을 이용하여 AccessToken을 갱신
        if(response.status === 401 && refresh_token){
            // TokenApiController 실행
            fetch("/api/token",{
                method : "POST",
                headers:{
                    Authorization: "Bearer" + localStorage.getItem("access_token"),
                    "Content-Type":"application/json",
                },
                // RefreshToken
                body:JSON.stringify({
                    refreshToken : getCookie("refresh_token"),
                }),
            }).then((res) =>{
                if(res.ok){
                    return res.json();
                }
            }).then((result)=>{
                // AccessToken이 잘 갱신된 경우 localStorage에 AccessToken을 저장
                localStorage.setItem("access_token", result.accessToken);
                // 다시 한번 이전에 실행했던 처리(추가,수정,삭제)를 실행
                httpRequest(method, url, body, success, fail);
            }).catch((error)=>fail());
        }else{
            return fail();
        }
    })
}










