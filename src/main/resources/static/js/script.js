const toggleSideBar=()=>{
    const sidebar =  document.getElementsByClassName("sidebar")[0];
    const content =  document.getElementsByClassName("content")[0];

    if(window.getComputedStyle(sidebar).display === "none"){
        sidebar.style.display = "block";
        content.style.marginLeft = "20%";
    }
    else{
        sidebar.style.display = "none";
        content.style.marginLeft = "0%";
    }
}
const search=()=>{
    let query=$("#search-input").val()
    if (query===''){
        $(".search-result").hide()
    }else{
        let url=`http://localhost:8080/search/${query}`
        fetch(url).then(response=>{
            return response.json()
        }).then(data=>{
            console.log(data)
            let text=`<div class='list-group'> `
             data.forEach(element=>{
                 text+=`<a href='/user/${element.cid}/contact' class='list-group-item list-group-item-action' >${element.name}</a> `
             })
            text+=`</div>`
            $(".search-result").html(text)
            $(".search-result").show()
        })

    }
}

const paymentStart=()=>{
   var amount=$("#payment_field").val()
    if (amount==='' || amount===null){
        swal("Failed!!", "Amount Required", "error");
        return
    }
    $.ajax({
        url:"/user/create_order",
        data:JSON.stringify({amount:amount,info:"order_request"}),
        contentType:"application/json",
        type:"POST",
        dataType:"json",
        success:function (response){
           if (response.status==='created'){
               let options={
                   key:"rzp_test_WOouOhXyXspViP",
                   amount:response.amount,
                   currency:"INR",
                   name:"Contact Management System",
                   description:"Donation",
                   image:"https://images.unsplash.com/photo-1606240724602-5b21f896eae8?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1113&q=80",
                   order_id:response.id,
                   handler:function (response){
                       console.log(response.razorpay_payment_id)
                       console.log(response.razorpay_order_id)
                       console.log(response.razorpay_signature)
                       updatePaymentOnServer(response.razorpay_payment_id,response.razorpay_order_id,"paid")
                     //  console.log("Payment Successful")

                   },
                   "prefill": {
                       "name": "", //your customer's name
                       "email": "",
                       "contact": ""
                   },
                   "notes": {
                       "address": "NIT Warangal"
                   },
                   "theme": {
                       "color": "#3399cc"
                   }
               }
               var rzp = new Razorpay(options);
               rzp.on("payment_failed",function (response){
                   console.log(response.error.code)
                   console.log(response.error.description)
                   console.log(response.error.source)
                   console.log(response.error.step)
                   console.log(response.error.reason)
                   console.log(response.error.metadata.order_id)
                   console.log(response.error.metadata.payment_id)
                   swal("Failed!!", "Oops!! Payment failed", "error");
               })
               rzp.open()

           }
        },
        error:function (error){
            console.log(error)
            alert("Something Went Wrong!!")
        }
    })
}
function updatePaymentOnServer(payment_id,order_id,status){
    $.ajax({
        url:"/user/update_order",
        data:JSON.stringify({payment_id:payment_id,order_id:order_id,status:status}),
        contentType:"application/json",
        type:"POST",
        dataType:"json",
        success:function (response){
            swal("Good job!", "Congrats!! Payment Successful", "success");
        },
        error:function (error){
            swal("Failed!!", "Your Payment is successful but we did not get on server,we will contact you as soon as possible", "error");
        }
    })
}
