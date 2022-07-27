<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;

class WallSeeder extends Seeder
{
    /**
     * Run the database seeds.
     *
     * @return void
     */
    public function run()
    {
        \App\Models\Wall::factory(5)->create();
    }
}
