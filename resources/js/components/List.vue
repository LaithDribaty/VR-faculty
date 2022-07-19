<template>

    <div class="mt-3">
        <div class="row">
            <div class="col-4">
                <div class="border border-dark rounded-pill h-9 position-relative">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" viewBox="0 0 20 20" aria-labelledby="search" role="presentation" class="position-absolute h-100 top-0" style="left: 10px;">
                        <path fill-rule="nonzero" d="M14.32 12.906l5.387 5.387a1 1 0 0 1-1.414 1.414l-5.387-5.387a8 8 0 1 1 1.414-1.414zM8 14A6 6 0 1 0 8 2a6 6 0 0 0 0 12z"></path>
                    </svg>
                    <input type="text" class="w-100 border-0 rounded-pill px-5 py-2" placeholder="search">
                </div>
            </div>
        </div>
    </div>

    <div class="mt-3 border border-0 rounded shadow-sm bg-white">

        <div class="p-3">
            <select class="form-select w-25" name="pageSize" aria-label="Default select example">
                <option selected>select page size</option>
                <option value="10">10</option>
                <option value="25">25</option>
                <option value="50">50</option>
                <option value="100">100</option>
            </select>
        </div>
        <div class="row mainbg fw-bolder px-3 py-2">
            <div class="col-3">
                #
            </div>
            <div class="col-3">
                Location
            </div>
            <div class="col-3">
                Size
            </div>
            <div class="col-3">
                Price
            </div>
        </div>

        <div class="row p-3">
            <div class="col-3">
                1
            </div>
            <div class="col-3">
                7th project
            </div>
            <div class="col-3">
                200 meters
            </div>
            <div class="col-3">
                300 millions
            </div>
        </div>

        <div class="row p-3">
             <div class="col-3">
                2
            </div>
            <div class="col-3">
                the sand
            </div>
            <div class="col-3">
                100 meters
            </div>
            <div class="col-3">
                150 millions
            </div>
        </div>

        <div class="row p-3">
             <div class="col-3">
                3
            </div>
            <div class="col-3">
                daa'toor
            </div>
            <div class="col-3">
                150 meters
            </div>
            <div class="col-3">
                200 millions
            </div>
        </div>

    </div>

</template>


<script>

    export default {

        data() {
            return {
                houses: [],
                house: {
                    id: '',
                    location: '',
                    size: '',
                    price: ''
                },
                hosue_id: '',
                pagination: {},
            }
        },
        props: [
            'type'
        ],

        created() {
            this.fetchItems();
        },

        methods: {
            fetchItems() {
                url = route(this.type);
                fetch(url)
                .then(res => res.json())
                .then(res => {
                    console.log(res);
                    this.houses = res;
                });
            }
        }
    }

</script>