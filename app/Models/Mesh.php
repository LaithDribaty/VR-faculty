<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Mesh extends Model
{
    use HasFactory;
    protected $fillable = [
        'size',
        'name',
        'url'
    ];
    
    /**
     * The table associated with the model.
     *
     * @var string
     */
    protected $table = 'meshs';

    public function objects()
    {
        return $this->hasMany(ObjectModel::class);
    }
}
