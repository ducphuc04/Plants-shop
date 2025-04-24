document.addEventListener("DOMContentLoaded", function () {
  let isOrderDetail =
    document.getElementById("processBtn") !== null &&
    document.getElementById("cancelBtn") !== null;
  if (isOrderDetail) {
    const urlParams = new URLSearchParams(window.location.search);
    const status = urlParams.get("status");

    const statusElement = document.getElementById("status");
    const buttonsElement = document.getElementById("button");
    const returnButtonElement = document.getElementById("return-btn");

    if (status === "processed") {
      statusElement.innerHTML = "<span>processed</span>";
      buttonsElement.classList.add("d-none");
      returnButtonElement.classList.remove("d-none");
    } else if (status === "canceled") {
      statusElement.innerHTML = "<span>canceled</span>";
      buttonsElement.classList.add("d-none");
      returnButtonElement.classList.remove("d-none");
    } else if (status === "pending") {
      statusElement.innerHTML = "<span>pending</span>";
    }

    function showProcessAnnounce() {
      Swal.fire({
        title: "Process",
        text: "Ypu processed this order",
        icon: "success",
        confirmButtonText: "OK",
      }).then((result) => {
        if (result.isConfirmed) {
          statusElement.innerHTML = "<span>processed</span>";
          buttonsElement.classList.add("d-none");
          returnButtonElement.classList.remove("d-none");
        }
      });
    }

    function showCancelAnnounce() {
      Swal.fire({
        title: "Cancel",
        text: "You canceled this order",
        icon: "success",
        confirmButtonText: "OK",
      }).then((result) => {
        if (result.isConfirmed) {
          statusElement.innerHTML = "<span>canceled</span>";
          buttonsElement.classList.add("d-none");
          returnButtonElement.classList.remove("d-none");
        }
      });
    }

    if (isOrderDetail) {
      document
        .getElementById("processBtn")
        .addEventListener("click", function () {
          showProcessAnnounce();
        });

      document
        .getElementById("cancelBtn")
        .addEventListener("click", function () {
          showCancelAnnounce();
        });
    }
  }
});
