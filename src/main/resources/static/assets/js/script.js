document.addEventListener("DOMContentLoaded", function () {
  let isEditPage = document.getElementById("editProductBtn") !== null;
  let isAddPage = document.getElementById("addProductBtn") !== null;
  let isCreatePage = document.getElementById("createAccountBtn") !== null;
  let isCheckoutPage = document.getElementById("order-form") !== null;

  function showFalseAnnounce() {
    Swal.fire({
      title: "Error!",
      text: "Please fill full information",
      icon: "error",
      confirmButtonText: "OK",
    });
    // return;
  }

  function showTrueAnnounce() {
    Swal.fire({
      title: "Update successfully",
      text: "Product is updated",
      icon: "success",
      confirmButtonText: "OK",
    }).then((result) => {
      if (result.isConfirmed) {
        if (!isCreatePage) window.location.href = "admin-product.html";
        else window.location.href = "admin-employees.html";
      }
    });
  }

  function showCheckOutSuccessfully() {
    Swal.fire({
      title: "Checkout successfully",
      text: "You checked out",
      icon: "success",
      confirmButtonText: "OK",
    }).then((result) => {
      if (result.isConfirmed) {
        window.location.href = "success.html";
      }
    });
  }

  function validateForm() {
    let productName = document.querySelector("input[type='text']").value.trim();
    let price = document.querySelectorAll("input[type='text']")[1].value.trim();
    let image = document.getElementById("uploadImage").files.length;

    if (isEditPage) {
      let saleOff = document
        .querySelectorAll("input[type='text']")[2]
        .value.trim();
      let type = document
        .querySelectorAll("input[type='text']")[3]
        .value.trim();
      if (!productName || !price || !type || image === 0) {
        showFalseAnnounce();
        return false;
      }
    }

    if (isAddPage) {
      let description = document.getElementById("description").value.trim();
      let type = document
        .querySelectorAll("input[type='text']")[2]
        .value.trim();
      if (!productName || !price || !type || !description || image === 0) {
        showFalseAnnounce();
        return false;
      }
    }

    return true;
  }

  function validateFormCreateAccount() {
    let username = document.querySelector("input[type='text']").value.trim();
    let password = document
      .querySelectorAll("input[type='password']")[0]
      .value.trim();
    // let role = document.querySelectorAll("input[type='text']")[1].value.trim();
    let address = document
      .querySelectorAll("input[type='text']")[1]
      .value.trim();
    let phone = document.querySelectorAll("input[type='text']")[2].value.trim();
    if (!username || !password || !address || !phone) {
      showFalseAnnounce();
      return false;
    }
    return true;
  }
  if (isEditPage) {
    document
      .getElementById("editProductBtn")
      .addEventListener("click", function () {
        if (validateForm()) {
          showTrueAnnounce();
        }
      });
  } else if (isAddPage) {
    document
      .getElementById("addProductBtn")
      .addEventListener("click", function () {
        if (validateForm()) {
          showTrueAnnounce();
        }
      });
  } else if (isCreatePage) {
    document
      .getElementById("createAccountBtn")
      .addEventListener("click", function () {
        if (validateFormCreateAccount()) {
          showTrueAnnounce();
        }
      });
  } else if (isCheckoutPage) {
    const form = document.getElementById("order-form");
    // const productInfor = document.getElementById("product-information");
    const bankOption = document.getElementById("bank-option");
    const qrModal = document.getElementById("qr-modal");
    const closeBtn = document.querySelector(".close1");

    document
      .getElementById("checkOutBtn")
      .addEventListener("click", function (event) {
        event.preventDefault();
        console.log("Hiiiii");
        if (bankOption.checked) {
          console.log("Hiiiii1");
          qrModal.style.display = "block";
        } else {
          console.log("Hiiiii2");
          showCheckOutSuccessfully();
        }
      });

    closeBtn.addEventListener("click", function () {
      qrModal.style.display = "none";
      console.log("Hiiiii13");
    });

    window.addEventListener("click", function (event) {
      if (event.target === qrModal) {
        qrModal.style.display = "none";
        console.log("Hiiiii14");
      }
    });
  }
  if (isAddPage || isEditPage) {
    document
      .getElementById("uploadImage")
      .addEventListener("change", function (event) {
        const file = event.target.files[0];
        if (file) {
          const reader = new FileReader();
          reader.onload = function (e) {
            const preview = document.getElementById("imagePreview");
            preview.src = e.target.result;
            preview.classList.remove("d-none");
          };
          reader.readAsDataURL(file);
        }
      });
  }
});
