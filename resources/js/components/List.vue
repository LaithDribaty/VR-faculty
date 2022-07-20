<template>

    <div class="mt-3">
        <div class="row">
            <div class="col-6 col-md-4">
                <div class="border border-dark rounded-pill h-9 position-relative">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" viewBox="0 0 20 20" aria-labelledby="search" role="presentation" class="position-absolute h-100 top-0" style="left: 10px;">
                        <path fill-rule="nonzero" d="M14.32 12.906l5.387 5.387a1 1 0 0 1-1.414 1.414l-5.387-5.387a8 8 0 1 1 1.414-1.414zM8 14A6 6 0 1 0 8 2a6 6 0 0 0 0 12z"></path>
                    </svg>
                    <input type="text" class="w-100 border-0 rounded-pill px-5 py-2" placeholder="search" v-model="q" @keyup="fetchItems()">
                </div>
            </div>

            <div class="col-4 d-none d-md-block">

            </div>

            <div class="col-6 col-md-4">
                <button class="btn btn-primary float-end h-100 w-50">
                    Add New 
                </button>
            </div>
        </div>
    </div>

    <div class="mt-3 border border-0 rounded shadow-sm bg-white">

        <div class="p-3">
            <select class="form-select w-50 border border-dark" name="per_page" aria-label="select items per page"  v-model="pagination.per_page" @change="fetchItems()">
                <option selected>select page size</option>
                <option value="10">10</option>
                <option value="25">25</option>
                <option value="50">50</option>
                <option value="100">100</option>
            </select>
        </div>
        <div class="row mainbg fw-bolder px-3 py-2">
            <div class="col-1">
                #
            </div>
            <div class="col-4">
                Location
            </div>
            <div class="col-4">
                Size
                <div class="float-end w-75">
                    <div id="size-slider" class="slider-styled mt-2"></div>
                </div>
            </div>
            <div class="col-3">
                Price
                <div class="float-end w-75">
                    <div id="price-slider" class="slider-styled mt-2"></div>
                </div>
            </div>
        </div>

        <div class="row p-3" v-for="item in items" v-bind:key="item.id">
            <div class="col-1">
                {{ item.id }}
            </div>
            <div class="col-4">
                {{ item.location }}
            </div>
            <div class="col-4">
                {{ item.size }} meters
            </div>
            <div class="col-3">
                {{ item.price }}$
            </div>
        </div>
        <hr>
        <div class="row pb-3 px-3 mb-5">
            <div class="col">
                <button class="btn btn-primary float-start" @click="fetchItems(pagination.prev_page_url)" v-bind:class="[{disabled: !pagination.prev_page_url}]">
                    previouse page
                </button>
            </div>
            <div class="col">
                <button class="btn btn-primary float-end" @click="fetchItems(pagination.next_page_url)" v-bind:class="[{disabled: !pagination.next_page_url}]">
                    next page
                </button>
            </div>
        </div>
    </div>

</template>


<script>
    import route from '../../../vendor/tightenco/ziggy/src/js';
    import * as noUiSlider from 'nouislider';
    import 'nouislider/dist/nouislider.css';

    export default {

        data() {
            return {
                items: [],
                item: { },
                item_id: '',
                pagination: { },
                q: null,
                size: {},
                price: {},
            }
        },

        props: [
            'type'
        ],

        created() {
            let urlParams = new URLSearchParams(window.location.search);

            this.pagination.per_page = urlParams.get('per_page');
            this.pagination.current_page = urlParams.get('page');
            this.q = urlParams.get('q');
            this.size = {min: urlParams.get('minSize') || 60, max: urlParams.get('maxSize') || 300};
            this.price = {min: urlParams.get('minPrice') || 0, max: urlParams.get('maxPrice') || 2000};
            this.fetchItems();
        },

        mounted() {
            const sizeSlider = document.getElementById('size-slider');
            noUiSlider.create(sizeSlider, {
                start: [this.size.min , this.size.max],
                step: 10,
                connect: true,
                tooltips: true,
                range: {
                    'min': 60,
                    'max': 300
                }
            });
            sizeSlider.noUiSlider.on('change', this.setSize);

            const priceSlider = document.getElementById('price-slider');
            noUiSlider.create(priceSlider, {
                start: [this.price.min , this.price.max],
                step: 10,
                connect: true,
                tooltips: true,
                range: {
                    'min': 0,
                    'max': 2000
                }
            });
            priceSlider.noUiSlider.on('change', this.setPrice);
        },

        methods: {
            fetchItems(url) {

                url = url || route(this.type, { 
                            _query: { 
                                per_page: this.pagination.per_page || 10,
                                page: 1,
                                q: this.q,
                                minSize: this.size.min,
                                maxSize: this.size.max,
                                minPrice: this.price.min,
                                maxPrice: this.price.max
                            } 
                        });
                
                fetch(url)
                .then(res => res.json())
                .then(res => {
                    this.items = res.data;
                    this.makePagination(res);
                    this.updateURL();
                })
                .catch(err => console.log(err))
                ;
            },
            makePagination(res) {
                let pagination = {
                    current_page: res.current_page,
                    next_page_url: res.next_page_url,
                    prev_page_url: res.prev_page_url,
                    per_page: res.per_page
                };
                this.pagination = pagination;
            },
            updateURL() {
                this.$router.push({fullPath: '/', path:"/" ,query: {
                    'q': this.q,
                    'per_page': this.pagination.per_page,
                    'page': this.pagination.current_page,
                    'minSize': this.size.min,
                    'maxSize': this.size.max,
                    'minPrice': this.price.min,
                    'maxPrice': this.price.max
                }});
            },
            setSize(values) {
                this.size.min = values[0];
                this.size.max = values[1];
                this.fetchItems();
            },
            setPrice(values) {
                this.price.min = values[0];
                this.price.max = values[1];
                this.fetchItems();
            }
        },
        
    }

</script>

<style>

.slider-styled,
.slider-styled .noUi-handle {
    box-shadow: none;
}

/* Hide markers on slider handles */
.slider-styled .noUi-handle::before,
.slider-styled .noUi-handle::after {
    display: none;
}

.slider-styled {
    border-radius: 0;
}

#size-slider {
    height: 14px;
}

#size-slider .noUi-connect {
    background: #212529;
}

#size-slider .noUi-handle {
    border-radius: 0;
    background: #0d6efd;
    height: 16px;
    width: 16px;
    top: -2px;
    right: -9px;
}

#price-slider {
    height: 14px;
}

#price-slider .noUi-connect {
    background: #212529;
}

#price-slider .noUi-handle {
    border-radius: 0;
    background: #0d6efd;
    height: 16px;
    width: 16px;
    top: -2px;
    right: -9px;
}
</style>